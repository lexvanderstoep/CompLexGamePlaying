package uk.co.complex.lvs.ggp.forms;

/**
 * The GameOutput interface represents an object which has the ability to output game state and
 * log game messages. It is used by <code>GameManager</code> to display game information.
 * @author Lex van der Stoep
 * @see uk.co.complex.lvs.ggp.GameManager
 */
public interface GameOutput {
	/**
	 * Print the given message.
	 * @param message Message to print
	 */
	public void print(Object message);
	
	/**
	 * Log the given message.
	 * @param message Message to log
	 */
	public void log(Object message);
}
