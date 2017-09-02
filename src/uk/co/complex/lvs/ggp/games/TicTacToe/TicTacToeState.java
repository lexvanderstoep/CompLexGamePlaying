package uk.co.complex.lvs.ggp.games.TicTacToe;

import java.util.Arrays;
import java.util.List;

import uk.co.complex.lvs.ggp.Player;
import uk.co.complex.lvs.ggp.State;
import uk.co.complex.lvs.ggp.games.TicTacToe.TicTacToe.BoxState;

public class TicTacToeState extends State {
	/* The board is indexed as follows:
	 * -------
	 * |0|1|2|
	 * |3|4|5|
	 * |6|7|8|
	 * -------
	 * 
	 * The first player is X, the second player is O.
	 */
	
	BoxState[] board;
	boolean xTurn;
	
	public TicTacToeState(List<Player> players) {
		super(players);
		board = new BoxState[9];
		xTurn = true;
		Arrays.fill(board, BoxState.empty);
	}
	
	@Override
	public TicTacToeState clone() {
		TicTacToeState newState = new TicTacToeState(getPlayers());
		System.arraycopy(board, 0, newState.board, 0, board.length);
		newState.xTurn = xTurn;
		return newState;
	}
	
	@Override
	public String toString() {
		String s = "";
		s += (xTurn?"X":"O") + "'s turn\n";
		for(int i = 0; i < 9; i++) {
			if (i != 0 & i % 3 == 0) {
				s += "\n";
			}
			String b = "";
			switch(board[i]) {
			case empty: b = Integer.toString(i);
						break;
			case X:		b = "X";
						break;
			case O:		b = "O";
						break;
			}
			s += b;
		}
		s += "\n\n";
		
		return s;
	}
}