package uk.co.complex.lvs.ggp.games.connectfour;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;

import uk.co.complex.lvs.ggp.*;

public class ConnectFourHuman extends Player{

	public ConnectFourHuman(String name) {
		super(name);
	}

	@Override
	public Move getNextMove(State s, StateMachine m, int time) {
		List<Move> allMoves = m.getMoves(s, this);
		if (allMoves.size() == 1)  {
			return allMoves.get(0);
		}

		System.out.println(getName() + ": In which column do you want to put a disc (-1 for null move)? ");
		Scanner in = new Scanner(System.in);
		int idx = in.nextInt();

		return new ConnectFourMove(this, idx);
	}

	@Override
	public Player clone() {
		return new ConnectFourHuman(getName());
	}

}
