package uk.co.complex.lvs.ggp.games.connectfour;

import uk.co.complex.lvs.ggp.Move;
import uk.co.complex.lvs.ggp.Player;

public class ConnectFourMove extends Move {
	// The value -1 represents the null move
	int index;

	public ConnectFourMove(Player player, int idx) {
		super(player);
		index = idx;
	}

	public static ConnectFourMove getNullMove(Player player) {
		return new ConnectFourMove(player, -1);
	}
}
