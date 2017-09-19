package uk.co.complex.lvs.ggp.games.tictactoe;

import java.util.Scanner;

import uk.co.complex.lvs.ggp.Move;
import uk.co.complex.lvs.ggp.Player;
import uk.co.complex.lvs.ggp.State;
import uk.co.complex.lvs.ggp.StateMachine;

/**
 * Represents a human player. This player class will interact using the console to determine the 
 * next move (asks the user).
 * @author Lex van der Stoep
 */
public class TicTacToeHuman extends Player {

	public TicTacToeHuman(String name) {
		super(name);
	}
	
	@Override
	public Move getNextMove(State s, StateMachine m, int time) {
		System.out.println("In which box do you want to put a mark (-1 for null move)? ");
		Scanner in = new Scanner(System.in);
		int idx = in.nextInt();
		
		return new TicTacToeMove(this, idx);
	}

	@Override
	public Player clone() {
		return new TicTacToeHuman(getName());
	}

}