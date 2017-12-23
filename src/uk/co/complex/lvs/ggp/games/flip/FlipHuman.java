package uk.co.complex.lvs.ggp.games.flip;

import uk.co.complex.lvs.ggp.*;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Represents a human player. This player class will interact using the console to determine the
 * next move (asks the user).
 */
public class FlipHuman extends Player{

    public FlipHuman(String name) {
        super(name);
    }

    @Override
    public Player clone() {
        return new FlipHuman(getName());
    }

    @Override
    public Move getNextMove(State s, StateMachine m, int time) {
        int x,y;
        try {
            FlipState state = (FlipState) s;
            boolean playerIsWhite = (this == state.getPlayers().get(0));
            boolean playersTurn = playerIsWhite == state.wTurn;
            if (playersTurn) {
                System.out.println("(" + getName() + ") In which cell do you want to put a mark (-1 -1 " +
                        "for null move)? ");
                Scanner in = new Scanner(System.in);
                x = in.nextInt();
                y = in.nextInt();
            } else {
                return FlipMove.getNullMove(this);
            }
        } catch (InputMismatchException e) {
            System.out.println("That move was invalid, correct usage is <x> <y>");
            return FlipMove.getNullMove(this);
        }

        return new FlipMove(this, x, y);
    }
}
