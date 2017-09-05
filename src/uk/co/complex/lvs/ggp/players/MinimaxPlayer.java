package uk.co.complex.lvs.ggp.players;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.complex.lvs.ggp.*;

/**
 * The MinimaxPlayer uses the Minimax algorithm to determine its next move. It searches the entire
 * game tree in order to find a "best" possible move. It does so by trying to maximise the score 
 * it can guarantee to achieve. In other words, it assumes that the opponent(s) choose the moves 
 * which will minimise our score and then finds the moves we can make, to maximise our score.
 * @author Lex van der Stoep
 */
public class MinimaxPlayer extends Player {

	public MinimaxPlayer(String name) {
		super(name);
	}

	@Override
	public Move getNextMove(State s, StateMachine m) {
		List<Player> players = s.getPlayers();
		if (players.size() != 2) throw new IllegalArgumentException("The Minimax algorithm was "
				+ "implemented for a two-player game.");
		
		// If there is only one legal move available, choose that one.
		List<Move> moves = m.getMoves(s, this);
		if (moves.size() == 1) return moves.get(0);
		
		// Run the Minimax algorithm to determine moves
		return maximin(s, m, Integer.MAX_VALUE).bestMove;
	}
	
	/**
	 * Uses the minimax algorithm to calculate the maximum guaranteed score that can be achieved by
	 * this player. For that, it makes the pessimistic assumption that the opponent will try to
	 * minimise our score. It makes use of alpha-beta pruning.
	 */
	private MiniMaxResult maximin(State s, StateMachine m, int beta) {
		// If the current state is terminal, return its value
		if (m.isTerminal(s)) {
			return new MiniMaxResult(null, m.getScores(s).get(this));
		}
		
		List<Player> players = s.getPlayers();
		// Get the opponent
		Player opponent = (this == players.get(0)) ? players.get(1) : players.get(0);

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
				Map<Player, Move> moves = new HashMap<>(2);
				moves.put(this, possiblePlayerMove);
				moves.put(opponent, possibleOpponentMove);
				int val;
				
				// Check the maximin value of the next state
				try {
					val = maximin(m.getNextState(s, moves), m, min).maximin;
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

		return new MiniMaxResult(bestMove, max);
	}
	
	private class MiniMaxResult {
		Move bestMove;
		int maximin;
		
		public MiniMaxResult(Move move, int val) {
			bestMove = move;
			maximin = val;
		}
	}
}