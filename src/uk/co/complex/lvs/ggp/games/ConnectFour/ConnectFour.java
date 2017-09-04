package uk.co.complex.lvs.ggp.games.ConnectFour;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.complex.lvs.ggp.*;
import uk.co.complex.lvs.ggp.games.ConnectFour.ConnectFourState.BoxState;

public class ConnectFour implements StateMachine {
	private final int winScore = 100;
	private final int loseScore = -100;
	private final int drawScore = 0;
	
	@Override
	public List<Move> getMoves(State s, Player p) {
		ConnectFourState state = (ConnectFourState) s;
		List<Player> players = state.getPlayers();
		Player currentPlayer = state.redTurn?players.get(0):players.get(1);
		
		List<Move> moves = new ArrayList<Move>();
		
		if (p != currentPlayer) {
			// Return the null move
			ConnectFourMove move = ConnectFourMove.getNullMove(p);
			moves.add(move);
		} else {
			// Return all possible moves (columns of the board which are not fully filled yet)
			int height = state.board[0].length;
			for (int col = 0; col < state.board.length; col++) {
				if (state.board[col][height - 1] == BoxState.empty) {
					ConnectFourMove move = new ConnectFourMove(p, col);
					moves.add(move);
				}
			}
		}
		
		return moves;
	}

	@Override
	public State getNextState(State s, Map<Player, Move> moves) throws IllegalMoveException {
		ConnectFourState state = (ConnectFourState) s.clone();
		List<Player> players = state.getPlayers();
		Player currentPlayer = state.redTurn?players.get(0):players.get(1);
		
		// Check the moves for all the players. If it is not the player's turn, then the move 
		// should be the null move. If it is the player's turn, then the move should be 
		// checked for validity.
		for (Player p: moves.keySet()) {
			ConnectFourMove m = (ConnectFourMove) moves.get(p);
			
			if (p == currentPlayer) {
				// Check if the column index is not out of bounds
				if (m.index < 0 || m.index > state.board.length) {
					throw new IllegalMoveException(state, m);
				}
				
				// Find the row index of the first empty cell in the column
				int row = -1;
				for (int i = 0; i < state.board[m.index].length; i++) {
					if (state.board[m.index][i] == BoxState.empty) {
						row = i;
						break;
					}
				}
				
				// If no empty cell was found, then the column is full and the move is invalid
				if (row < 0) {
					throw new IllegalMoveException(state, m);
				}
				
				// Update the board
				state.board[m.index][row] = state.redTurn?BoxState.R:BoxState.Y;
			} else {
				// If it is not the player's turn, then check if it's the null move
				if (m.index != -1) {
					throw new IllegalMoveException(state, m);
				}
			}
		}
		
		// Change who's turn it is
		state.redTurn = !state.redTurn;
		
		return state;
	}

	@Override
	public boolean isTerminal(State s) {
		ConnectFourState state = (ConnectFourState) s;
		
		Map<Player, Integer> scores = getScores(state);
		List<Player> players = state.getPlayers();
		
		// If any of the scores is not the draw score, then it is a terminal state
		if (scores.get(players.get(0)) != drawScore) return true;
		if (scores.get(players.get(1)) != drawScore) return true;
		
		// Check if there is an empty column left
		for (int col = 0; col < state.board.length; col++) {
			int height = state.board[col].length;
			if (state.board[col][height - 1] == BoxState.empty) return false;
		}
		
		return true;
	}

	@Override
	public Map<Player, Integer> getScores(State s) {
		ConnectFourState state = (ConnectFourState) s;
		List<Player> players = state.getPlayers();
		
		// Fill the score table with the scores of an non terminal game
		Map<Player, Integer> scores = new HashMap<>();
		scores.put(players.get(0), drawScore);
		scores.put(players.get(1), drawScore);
		
		BoxState winner = BoxState.empty;
		
		boolean found = false;
		
		// Check vertical lines
		for (int c = 0; c < state.board.length; c++) {
			for (int r = 0; r < state.board[0].length - 3; r++) {
				winner = checkWinner(state.board, c, r, c, r + 1, c, r + 2, c, r + 3);
				if (winner != BoxState.empty) {
					found = true;
					break;
				}
			}
			if (found) break;
		}

		if (!found) {
			// Check horizontal lines
			for (int r = 0; r < state.board[0].length; r++) {
				for (int c = 0; c < state.board.length - 3; c++) {
					winner = checkWinner(state.board, c, r, c + 1, r, c + 2, r, c + 3, r);
					if (winner != BoxState.empty) {
						found = true;
						break;
					}
				}
				if (found) break;
			}
		}

		if (!found) {
			// Check diagonal lines (left-up to right-down)
			for (int rowStart = 3; rowStart < state.board[0].length; rowStart++) {
				// Check for four-in-a-row from (0, rowStart) on
				for (int i = 0; i <= rowStart - 3; i++) {
					int r = rowStart - i;
					int c = i;
					winner = checkWinner(state.board, c, r, c + 1, r - 1, c + 2, r - 2, c + 3, r - 3);
					if (winner != BoxState.empty) {
						found = true;
						break;
					}
				}
				if (found) break;
			}
		}
		if (!found) {
			for (int colStart = 1; colStart < state.board.length - 3; colStart++) {
				// Check for four-in-a-row from (colStart, height-1) on
				for (int i = 0; i <= (3- colStart); i++) {
					int r = state.board[0].length - 1 - i;
					int c = colStart + i;
					winner = checkWinner(state.board, c, r, c + 1, r - 1, c + 2, r - 2, c + 3, r - 3);
					if (winner != BoxState.empty) {
						found = true;
						break;
					}
				}
				if (found) break;
			}
		}

		if (!found) {
			// Check diagonal lines (right-up to left-down)
			for (int rowStart = 3; rowStart < state.board[0].length; rowStart++) {
				// Check for four-in-a-row from (width-1, rowStart) on
				for (int i = 0; i <= rowStart - 3; i++) {
					int r = rowStart - i;
					int c = state.board.length - 1 - i;
					winner = checkWinner(state.board, c, r, c - 1, r - 1, c - 2, r - 2, c - 3, r - 3);
					if (winner != BoxState.empty) {
						found = true;
						break;
					}
				}
				if (found) break;
			}
		}
		if (!found) {
			for (int colStart = state.board.length - 2; colStart >= 3; colStart--) {
				// Check for four-in-a-row from (colStart, height-1) on
				for (int i = 0; i <= colStart - 3; i++) {
					int r = state.board[0].length - 1 - i;
					int c = colStart - i;
					winner = checkWinner(state.board, c, r, c - 1, r - 1, c - 2, r - 2, c - 3, r - 3);
					if (winner != BoxState.empty) {
						found = true;
						break;
					}
				}
				if (found) break;
			}
		}
		
		if (winner == BoxState.R) {
			scores.put(players.get(0), winScore);
			scores.put(players.get(1), loseScore);
		} else if (winner == BoxState.Y) {
			scores.put(players.get(0), loseScore);
			scores.put(players.get(1), winScore);
		}
		return scores;
	}

	@Override
	public State getInitialState(List<Player> players) {
		if (players.size() != 2) throw new IllegalArgumentException("Connect Four requires exactly "
				+ "two players. There were " + players.size() + " players provided.");
		ConnectFourState initialState = new ConnectFourState(players);
		return initialState;
	}
	
	private BoxState checkWinner(BoxState[][] board, int c1, int r1, int c2, int r2, int c3, 
			int r3, int c4, int r4) {
		// Checks whether the four points (c1, r1), (c2, r2), ... are all occupied by one player.
		// If so, return that player. Else, return empty.
		
		int Rs = 0;
		int Ys = 0;
		
		if (board[c1][r1] == BoxState.R) Rs++;
		if (board[c1][r1] == BoxState.Y) Ys++;
		if (board[c2][r2] == BoxState.R) Rs++;
		if (board[c2][r2] == BoxState.Y) Ys++;
		if (board[c3][r3] == BoxState.R) Rs++;
		if (board[c3][r3] == BoxState.Y) Ys++;
		if (board[c4][r4] == BoxState.R) Rs++;
		if (board[c4][r4] == BoxState.Y) Ys++;
		
		if (Rs == 4) return BoxState.R;
		if (Ys == 4) return BoxState.Y;
		return BoxState.empty;
	}
}
