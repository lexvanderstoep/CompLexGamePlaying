package uk.co.complex.lvs.ggp;

/**
 * Represents a move. A move applied to a state gives a new state.
 * @author Lex van der Stoep
 */
public abstract class Move {
	private Player mPlayer;
	
	public Move (Player player) {
		mPlayer = player;
	}
	
	public Player getPlayer() {
		return mPlayer;
	}
}