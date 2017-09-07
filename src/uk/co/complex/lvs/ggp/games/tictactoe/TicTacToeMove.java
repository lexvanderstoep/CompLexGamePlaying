package uk.co.complex.lvs.ggp.games.tictactoe;

import uk.co.complex.lvs.ggp.Move;
import uk.co.complex.lvs.ggp.Player;

public class TicTacToeMove extends Move {
	// The value -1 represents the null move
	int index;
	
	public TicTacToeMove(Player player, int idx) {
		super(player);
		index = idx;
	}
	
	public static TicTacToeMove getNullMove(Player player) {
		return new TicTacToeMove(player, -1);
	}
}