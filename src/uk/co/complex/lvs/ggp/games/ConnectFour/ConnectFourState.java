package uk.co.complex.lvs.ggp.games.ConnectFour;

import java.util.Arrays;
import java.util.List;

import uk.co.complex.lvs.ggp.Player;
import uk.co.complex.lvs.ggp.State;

public class ConnectFourState extends State {
	/* The Connect Four board will be 7x6 (7 columns, 6 rows). The index of the bottom-left element
	 * is (0, 0). The index of the top-right element is (6, 5).
	 * 
	 * The first player is R (red), the second player is Y (yellow).
	 */
	
	BoxState[][] board;
	boolean redTurn;

	public ConnectFourState(List<Player> players) {
		super(players);
		board = new BoxState[7][6];
		redTurn = true;
		for (BoxState[] column : board) Arrays.fill(column, BoxState.empty);
	}

	@Override
	public State clone() {
		ConnectFourState newState = new ConnectFourState(getPlayers());
		for (int i = 0; i < board.length; i++) {
			System.arraycopy(board[i], 0, newState.board[i], 0, newState.board[i].length);
		}
		newState.redTurn = redTurn;
		return newState;
	}
	
	@Override
	public String toString() {
		String s = "";
		s += (redTurn?"Red":"Yellow") + "'s turn\n";
		for (int row = board[0].length - 1; row >= 0; row--) {
			String line = "";
			for (int col = 0; col < board.length; col++) {
				switch(board[col][row]) {
				case empty:	line += " ";
							break;
				case R:		line += "R";
							break;
				case Y:		line += "Y";
							break;
				}
			}
			line += "\n";
			s += line;
		}
		s += "\n";
		
		return s;
	}
	
	public enum BoxState {
		empty, R, Y
	}

}
