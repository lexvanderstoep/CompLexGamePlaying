package uk.co.complex.lvs.ggp.players;

import java.util.List;
import java.util.Random;

import uk.co.complex.lvs.ggp.Move;
import uk.co.complex.lvs.ggp.Player;
import uk.co.complex.lvs.ggp.State;
import uk.co.complex.lvs.ggp.StateMachine;

/**
 * The RandomPlayer is a simple player. When asked for a next move, it returns a randomly selected
 * move from the list of possible moves.
 * @author Lex van der Stoep
 */
public class RandomPlayer extends Player {

	public RandomPlayer(String name) {
		super(name);
	}
	
	@Override
	public Move getNextMove(State s, StateMachine m, int time) {
		List<Move> moves = m.getMoves(s, this);
		
		Random rnd = new Random();
		int idx = rnd.nextInt(moves.size());
		
		return moves.get(idx);
	}

}
