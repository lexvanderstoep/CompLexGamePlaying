package uk.co.complex.lvs.ggp.games.connectfour;

import java.util.Scanner;

import uk.co.complex.lvs.ggp.Move;
import uk.co.complex.lvs.ggp.Player;
import uk.co.complex.lvs.ggp.State;
import uk.co.complex.lvs.ggp.StateMachine;

public class ConnectFourHuman extends Player {

	public ConnectFourHuman(String name) {
		super(name);
	}

	@Override
	public Move getNextMove(State s, StateMachine m, int time) {
		System.out.println("In which column do you want to put a disc (-1 for null move)? ");
		Scanner in = new Scanner(System.in);
		int idx = in.nextInt();
		
		return new ConnectFourMove(this, idx);
	}

	@Override
	public Player clone() {
		return new ConnectFourHuman(getName());
	}

}
