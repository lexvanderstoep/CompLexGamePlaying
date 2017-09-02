package uk.co.complex.lvs.ggp;

/**
 * Thrown when it is not possible to calculate the next state when a certain move is applied to a
 * state.
 * @author Lex van der Stoep
 */
public class IllegalMoveException extends Exception {
	private State mState;
	private Move mMove;
	
	public IllegalMoveException(State state, Move move) {
		mState = state;
		mMove = move;
	}
	
	public State getState() {
		return mState;
	}
	
	public Move getMove() {
		return mMove;
	}
}
