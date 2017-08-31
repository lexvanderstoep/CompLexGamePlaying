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
}