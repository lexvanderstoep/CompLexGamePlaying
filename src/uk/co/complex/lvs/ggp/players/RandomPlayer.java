package uk.co.complex.lvs.ggp.players;

import java.util.List;
import java.util.Random;

import uk.co.complex.lvs.ggp.Move;
import uk.co.complex.lvs.ggp.Player;
import uk.co.complex.lvs.ggp.State;
import uk.co.complex.lvs.ggp.StateMachine;

public class RandomPlayer extends Player {

	public RandomPlayer() {
		super("Random");
	}
	
	@Override
	public Move getNextMove(State s, StateMachine m) {
		List<Move> moves = m.getMoves(s, this);
		
		Random rnd = new Random();
		int idx = rnd.nextInt(moves.size());
		
		return moves.get(idx);
	}

}
