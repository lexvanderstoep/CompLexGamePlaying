package uk.co.complex.lvs.ggp;

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
