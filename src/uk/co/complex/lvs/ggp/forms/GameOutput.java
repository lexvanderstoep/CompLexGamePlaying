package uk.co.complex.lvs.ggp.forms;

import uk.co.complex.lvs.ggp.State;

/**
 * The GameOutput interface represents an object which has the ability to output game state and
 * log game messages. It is used by <code>GameManager</code> to display game information.
 * @author Lex van der Stoep
 * @see uk.co.complex.lvs.ggp.GameManager
 */
public interface GameOutput {
	/**
	 * Print the current state of the game
	 * @param state State to print
	 */
	public void print(State state);
	
	/**
	 * Log the given message.
	 * @param message Message to log
	 */
	public void log(String message);
}
