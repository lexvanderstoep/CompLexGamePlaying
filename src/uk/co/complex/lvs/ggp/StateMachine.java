package uk.co.complex.lvs.ggp;

import java.util.List;
import java.util.Map;

/**
 * A StateMachine generates possible moves for a given state and computes the next state from a 
 * current state and a given move.
 * @author Lex van der Stoep
 */
public interface StateMachine {
	/**
	 * Computes the possible moves for the given player from a certain state.
	 * @param s Current state
	 * @param p Player to compute moves for
	 * @return The possible moves
	 */
	public List<Move> getMoves (State s, Player p);
	
	/**
	 * Applies the moves of all players to the given state.
	 * @param s Current state
	 * @param moves Moves to apply
	 * @return The next state
	 * @throws IllegalMoveException	It is not possible to apply the moves to the given state. 
	 */
	public State getNextState (State s, Map<Player, Move> moves) throws IllegalMoveException;
	
	/**
	 * Computes whether the given state is a terminal state.
	 * @param s Current state
	 * @return True if the state is terminal, false otherwise
	 */
	public boolean isTerminal (State s);
	
	/**
	 * Computes the score for each of the players
	 * @param s Current state
	 * @return
	 */
	public Map<Player, Integer> getScores(State s);
	
	/**
	 * Gets the initial state of the game. This essentially marks the start of a new game.
	 * @param players The players who will play the game
	 * @return The initial state
	 */
	public State getInitialState(List<Player> players);
}
