package uk.co.complex.lvs.ggp;

/**
 * A player represents the entity who plays the game. It is responsible for selecting a next move 
 * when asked for it by a game manager. A game player can be artificial as well as human.
 * @author Lex van der Stoep
 */
public abstract class Player {
	private String mName;
	
	public Player(String name) {
		mName = name;
	}
	
	public String getName() {
		return mName;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	/**
	 * The player computes the next move it wants to make given the game and its state.
	 * @param s Current state of the game
	 * @param m The StateMachine representing the concept of the game
	 * @return The move the players wants to make
	 */
	public abstract Move getNextMove(State s, StateMachine m);
}
