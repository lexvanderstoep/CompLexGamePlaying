package uk.co.complex.lvs.ggp.games.flip;

import uk.co.complex.lvs.ggp.Player;
import uk.co.complex.lvs.ggp.State;

import java.util.Arrays;
import java.util.List;

public class FlipState extends State {
    /* A Flip board is an NxN board. The cell are indexed as follows:
     * -----------------...-----------
     * | (0,0) | (1,0) |...| (N-1,0) |
     * | (1,0) | (1,1) |...| (N-1,1) |
     * :                ...          :
     * |(N-1,0)|(N-1,1)|...|(N-1,N-1)|
     * -----------------...-----------
     *
     * The first player is white, the second player is black.
     */

    BoxState[] board;
    boolean wTurn;
    final int N;

    public FlipState(List<Player> players, int N) {
        super(players);
        board = new BoxState[N*N];
        wTurn = true;
        this.N = N;
        Arrays.fill(board, BoxState.empty);
    }

    @Override
    public State clone() {
        FlipState newState = new FlipState(getPlayers(), N);
        System.arraycopy(board, 0, newState.board, 0, board.length);
        newState.wTurn = wTurn;
        return newState;
    }

    @Override
    public String toString() {
        char[] chars = new char[N];
        Arrays.fill(chars, '=');
        String s = (new String(chars)) + "\n";
        for(int i = 0; i < N*N; i++) {
            if (i != 0 & i % N == 0) {
                s += "\n";
            }
            String b = "";
            switch(board[i]) {
                case empty: b = "-";
                    break;
                case B:		b = "B";
                    break;
                case W:		b = "W";
                    break;
            }
            s += b;
        }

        // Print who's turn it is
        s += "\n";
        s += "Turn: " + (wTurn?getPlayers().get(0):getPlayers().get(1)) + " (" + (wTurn?"W":"B") + ")" + "\n";
        s += (new String(chars)) + "\n";
        s += "\n";

        return s;
    }

    public enum BoxState {
        empty, W, B;
    }
}
