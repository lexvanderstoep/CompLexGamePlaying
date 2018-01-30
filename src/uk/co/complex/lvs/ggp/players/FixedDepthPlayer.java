package uk.co.complex.lvs.ggp.players;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import uk.co.complex.lvs.ggp.IllegalMoveException;
import uk.co.complex.lvs.ggp.Move;
import uk.co.complex.lvs.ggp.Player;
import uk.co.complex.lvs.ggp.State;
import uk.co.complex.lvs.ggp.StateMachine;

/**
 * The FixedDepthPlayer builds upon the MinimaxPlayer. It uses the Minimax algorithm to search the
 * game tree and determine its next move. However, the difference is that the FixedDepthPlayer
 * stops searching (meaning it does not search any deeper) the game tree once it has reached a certain
 * fixed depth. At that point, a value is given to the current state by evaluating the state with
 * some heuristic function.
 * @author Lex van der Stoep
 */
public class FixedDepthPlayer extends Player {
	private static final int MAX_DEPTH = 2;
	private static final int NUM_OF_WALKS = 10;

	private Move move;
	private Random rnd = new Random();
	
	public FixedDepthPlayer(String name) {
		super(name);
	}

	@Override
	public Move getNextMove(State s, StateMachine m, int time) {
		List<Player> players = s.getPlayers();
		if (players.size() != 2) throw new IllegalArgumentException("The HeuristicPlayer was "
				+ "implemented for a two-player game.");
		
		// If there is only one legal move available, choose that one.
		List<Move> moves = m.getMoves(s, this);
		if (moves.size() == 1) return moves.get(0);
		
		// Run the Minimax algorithm to determine move
		maximin(s, m, Integer.MAX_VALUE, 0); //this sets move
		return move;
	}
	
	/**
	 * Uses the Minimax algorithm to calculate the maximum guaranteed score that can be achieved by
	 * this player. For that, it makes the pessimistic assumption that the opponent will try to
	 * minimise our score. It makes use of alpha-beta pruning.
	 * Once the algorithm reaches a certain search depth, the search is terminated (does not go any
	 * deeper). The value given to the current state is determined by some heuristic function.
	 */
	private int maximin(State s, StateMachine m, int beta, int currentDepth) {
		// If the current state is terminal, return its value
		if (m.isTerminal(s)) {
			return m.getScores(s).get(this);
		}
		
		// Get the players
		List<Player> players = s.getPlayers();
		Player opponent = (this == players.get(0)) ? players.get(1) : players.get(0);
		
		// Check if the maximum search depth has been reached. If so, evaluate the heuristic
		// function of the state
		if (currentDepth >= MAX_DEPTH) {
			return phi(s, m);
		}

		List<Move> playerMoves = m.getMoves(s, this);
		List<Move> opponentMoves = m.getMoves(s, opponent);

		Move bestMove = null;
		int max = Integer.MIN_VALUE;

		// Iterate over this player's and the opponent's available moves and determine the max 
		// score and best move.
		for (Move possiblePlayerMove : playerMoves) {
			int min = Integer.MAX_VALUE;

			for (Move possibleOpponentMove : opponentMoves) {
				// Apply the moves
				Map<Player, Move> moves = new HashMap<>();
				moves.put(this, possiblePlayerMove);
				moves.put(opponent, possibleOpponentMove);
				int val;
				
				// Check the maximin value of the next state
				try {
					val = maximin(m.getNextState(s, moves), m, min, currentDepth + 1);
				} catch (IllegalMoveException e) {
					val = Integer.MAX_VALUE;
				}
				if (val < min) min = val;
				
				// Alpha pruning
				if (min <= max) break;
			}

			if (min > max) {
				// Update best move
				max = min;
				bestMove = possiblePlayerMove;
			}
			
			// Beta pruning
			if (max >= beta) break;
		}

		if (currentDepth == 0) move = bestMove;
		return max;
	}
	
	/**
	 * Phi is a heuristic function to evaluate a non-terminal state. It determines its value using
	 * 'random walks'. From the given state on, it performs several random walks and averages the
	 * values of these walks. A random walk is a sequence of random moves until a terminal state is
	 * reached.
	 * @param s The state to be evaluated
	 * @param m The StateMachine which represents the concept of the game
	 * @return The heuristic value of the state s
	 */
	private int phi(State s, StateMachine m) {
		int totScore = 0;
		
		List<Player> players = s.getPlayers();
		Player opponent = (this == players.get(0)) ? players.get(1) : players.get(0);
		
		for (int i = 0; i < NUM_OF_WALKS; i++) {
			State tempState = s.clone();
			
			//Start walking until a terminal state is reached
			while (!m.isTerminal(tempState)) {
				List<Move> playerMoves = m.getMoves(tempState, this);
				List<Move> opponentMoves = m.getMoves(tempState, opponent);
				Map<Player, Move> moves = new HashMap<>();
				moves.put(this, getRandomMove(playerMoves));
				moves.put(opponent, getRandomMove(opponentMoves));
				
				// Apply the random moves
				try {
					tempState = m.getNextState(tempState, moves);
				} catch (IllegalMoveException e) {
					throw new AssertionError("The random moves played should have been valid");
				}
			}
			
			totScore += m.getScores(tempState).get(this);
		}
		
		// Return the average of the scors of the random walks
		return totScore/NUM_OF_WALKS;
	}
	
	private Move getRandomMove(List<Move> moves) {
		return moves.get(rnd.nextInt(moves.size()));
	}

	@Override
	public Player clone() {
		return new FixedDepthPlayer(getName());
	}
}
