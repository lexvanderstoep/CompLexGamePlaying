package uk.co.complex.lvs.ggp;

/**
 * A player represents the entity who plays the game. It is responsible for selecting a next move 
 * when asked for it by a game manager. A game player can be artificial as well as human.
 * @author Lex
 */
public abstract class Player {
	private String mName;
	
	public Player(String name) {
		mName = name;
	}
	
	public String getName() {
		return mName;
	}
	
	public abstract Move getNextMove(State s, StateMachine m);
}
