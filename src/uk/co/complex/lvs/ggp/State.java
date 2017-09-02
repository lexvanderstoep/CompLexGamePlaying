package uk.co.complex.lvs.ggp;

import java.util.List;

/**
 * Represents the state of a game.
 * @author Lex van der Stoep
 */
public abstract class State implements Cloneable {
	private List<Player> mPlayers;
	
	public State(List<Player> players) {
		mPlayers = players;
	}
	
	public List<Player> getPlayers() {
		return mPlayers;
	}
	
	@Override
	public abstract State clone();
}
