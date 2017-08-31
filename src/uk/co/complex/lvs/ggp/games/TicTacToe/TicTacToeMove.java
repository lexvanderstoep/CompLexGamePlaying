package uk.co.complex.lvs.ggp.games.TicTacToe;

import uk.co.complex.lvs.ggp.Move;
import uk.co.complex.lvs.ggp.Player;

public class TicTacToeMove extends Move {
	// The value -1 represents the null move
	int index;
	
	public TicTacToeMove(Player player, int idx) {
		super(player);
		index = idx;
	}
}