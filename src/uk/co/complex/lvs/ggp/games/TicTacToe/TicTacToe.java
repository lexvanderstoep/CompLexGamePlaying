package uk.co.complex.lvs.ggp.games.TicTacToe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.complex.lvs.ggp.*;

public class TicTacToe implements StateMachine {
	private final int winScore = 100;
	private final int loseScore = -100;
	private final int drawScore = 0;

	@Override
	public List<Move> getMoves(State s, Player p) {
		TicTacToeState state = (TicTacToeState) s;
		List<Player> players = state.getPlayers();
		Player currentPlayer = state.xTurn?players.get(0):players.get(1);
		
		List<Move> moves = new ArrayList<Move>();
		
		if (p != currentPlayer) {
			// Return the null move
			TicTacToeMove move = new TicTacToeMove(p, -1);
			moves.add(move);
		} else {
			// Return all possible moves (places which are still empty)
			for (int i = 0; i < 9; i++) {
				if (state.board[i] != BoxState.empty) {
					TicTacToeMove move = new TicTacToeMove(p, i);
					moves.add(move);
				}
			}
		}
		
		return moves;
	}

	@Override
	public State getNextState(State s, Move m) throws IllegalMoveException {
		TicTacToeState state = (TicTacToeState) s;
		TicTacToeMove move = (TicTacToeMove) m;
		List<Player> players = state.getPlayers();
		Player currentPlayer = state.xTurn?players.get(0):players.get(1);
		
		if (m.getPlayer() != currentPlayer) {
			// It is not this player's turn. Therefore, the move should be the null move
			if (move.index != -1) {
				throw new IllegalMoveException(state, move);
			}
		} else {
			// Check if the box selected as the move is empty
			if (state.board[move.index] != BoxState.empty) throw new IllegalMoveException(state, move);
			
			state.board[move.index] = state.xTurn?BoxState.X:BoxState.O;
		}
		
		return state;
	}

	@Override
	public boolean isTerminal(State s) {
		TicTacToeState state = (TicTacToeState) s;
		
		Map<Player, Integer> scores = getScores(state);
		List<Player> players = state.getPlayers();
		
		// If any of the scores is not the draw score, then it is a terminal state
		if (scores.get(players.get(0)) != drawScore) return true;
		if (scores.get(players.get(1)) != drawScore) return true;
		
		// Check if there is an empty box
		for (int i = 0; i < 9; i++) {
			if (state.board[i] == BoxState.empty) return false;
		}
		
		return true;
	}

	@Override
	public Map<Player, Integer> getScores(State s) {
		TicTacToeState state = (TicTacToeState) s;
		List<Player> players = s.getPlayers();
		
		Map<Player, Integer> scores = new HashMap<>();
		scores.put(players.get(0), drawScore);
		scores.put(players.get(1), drawScore);

		BoxState[] winner = new BoxState[8];
		
		// Check horizontal lines
		winner[0] = checkWinner(state, 0, 1, 2);
		winner[1] = checkWinner(state, 3, 4, 5);
		winner[2] = checkWinner(state, 6, 7, 8);
		// Check vertical lines
		winner[3] = checkWinner(state, 0, 3, 6);
		winner[4] = checkWinner(state, 1, 4, 7);
		winner[5] = checkWinner(state, 2, 5, 8);
		// Check diagonals
		winner[6] = checkWinner(state, 0, 4, 8);
		winner[7] = checkWinner(state, 2, 4, 6);
		
		// Check if any player had three in a row
		for (int i = 0; i < winner.length; i++) {
			if (winner[i] == BoxState.X) {
				scores.put(players.get(0), winScore);
				scores.put(players.get(1), loseScore);
			} else if (winner[i] == BoxState.O) {
				scores.put(players.get(0), loseScore);
				scores.put(players.get(1), winScore);
			}
		}
		
		return scores;
	}

	@Override
	public State getInitialState(List<Player> players) {
		TicTacToeState initialState = new TicTacToeState(players);
		return initialState;
	}
	
	
	
	public enum BoxState {
		empty, X, O
	}
	
	private BoxState checkWinner(TicTacToeState state, int idx1, int idx2, int idx3) {
		// Checks whether the three given indices are all occupied by one player.
		// If so, return that player. Else, return empty.
		
		int Xs = 0;
		int Os = 0;
		
		if (state.board[idx1] == BoxState.X) Xs++;
		if (state.board[idx1] == BoxState.O) Os++;
		if (state.board[idx2] == BoxState.X) Xs++;
		if (state.board[idx2] == BoxState.O) Os++;
		if (state.board[idx3] == BoxState.X) Xs++;
		if (state.board[idx3] == BoxState.O) Os++;
		
		if (Xs == 3) return BoxState.X;
		if (Os == 3) return BoxState.O;
		return BoxState.empty;
	}
}
