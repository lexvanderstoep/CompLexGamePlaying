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
 * The VariableDepthPlayer builds upon the HeuristicPlayer. It uses the Minimax algorithm to search
 * the game tree (until a certain depth) and determines its next move. When that certain is
 * reached, it evaluates the state there using a heuristic function. HeuristicPlayer is a
 * fixed-depth player, meaning that the depth until it searches is constant. VariableDepthPlayer
 * uses iterative deepening to search the game tree. It first does a fixed-depth search of the
 * tree with depth 1, then with depth 2, and so forth; until the player does not have enough time
 * left to search the tree again.
 * @author Lex van der Stoep
 */
public class VariableDepthPlayer extends Player {
	private long startTime;
	private long totTime;
	private static int MAX_DEPTH = 1;					// The max depth of the game tree search
														// (is altered during iterative deepening)
	private static final int NUM_OF_WALKS = 10;			// The number of random walks
	private static final long minTimeToRespond = 100;	// The number of milliseconds which the
														// player should at least have left when
														// returning its answer.
	
	private Move bestMove;
	private Random rnd = new Random();
	private boolean searchedCompletely = false;			// True iff the minimax algorithm has fully
														// searched the entire game tree
	
	public VariableDepthPlayer(String name) {
		super(name);
	}

	@Override
	public Move getNextMove(State s, StateMachine m, int time) {
		// Set the start time to keep track of the elapsed time.
		// This is to help the player in responding in time.s
		startTime = System.currentTimeMillis();
		totTime = time;
		
		List<Player> players = s.getPlayers();
		if (players.size() != 2) throw new IllegalArgumentException("The Minimax algorithm was "
				+ "implemented for a two-player game.");
		
		// If there is only one legal move available, choose that one.
		List<Move> moves = m.getMoves(s, this);
		if (moves.size() == 1) return moves.get(0);

		// Perform iterative deepening as long as there is enough time left and it has not yet
		// fully searched the game tree
		MAX_DEPTH = 1;
		searchedCompletely = false;
		while (!searchedCompletely & getTimeLeft() > minTimeToRespond) {
			// Run the Minimax algorithm to determine move
			searchedCompletely = true;
			maximin(s, m, Integer.MAX_VALUE, 0); // this sets bestMove, if it ran succesfully
			MAX_DEPTH++;
		}
		return bestMove;
	}
	
	/**
	 * Uses the Minimax algorithm to calculate the maximum guaranteed score that can be achieved by
	 * this player. For that, it makes the pessimistic assumption that the opponent will try to
	 * minimise our score. It makes use of alpha-beta pruning.
	 * Once the algorithm reaches a certain search depth, the search is terminated (does not go any
	 * deeper). The value given to the current state is determined by some heuristic function.
	 * @return The minimum guaranteed score that can be achieved by this player.
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
		// function of the state. At this point it is certain that the tree has not been fully
		// searched.
		if (currentDepth >= MAX_DEPTH) {
			searchedCompletely = false;
			return phi(s, m);
		}

		List<Move> playerMoves = m.getMoves(s, this);
		List<Move> opponentMoves = m.getMoves(s, opponent);

		Move move = null;
		int max = Integer.MIN_VALUE;

		// Iterate over this player's and the opponent's available moves and determine the max 
		// score and best move.
		for (Move possiblePlayerMove : playerMoves) {
			int min = Integer.MAX_VALUE;

			for (Move possibleOpponentMove : opponentMoves) {
				// If there is not enough time left, stop the recursive calls
				if (getTimeLeft() < minTimeToRespond) return Integer.MIN_VALUE;
				
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
				move = possiblePlayerMove;
			}
			
			// Beta pruning
			if (max >= beta) break;
		}

		// Only update the bestMove if there was enough time to search the tree
		if (currentDepth == 0 && getTimeLeft() > minTimeToRespond) {
			bestMove = move;
		}
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
	
	private long getTimeLeft() {
		return (startTime + totTime - System.currentTimeMillis());
	}
}
