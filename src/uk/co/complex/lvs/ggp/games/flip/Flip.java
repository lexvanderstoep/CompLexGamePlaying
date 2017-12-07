package uk.co.complex.lvs.ggp.games.flip;

import uk.co.complex.lvs.ggp.*;
import uk.co.complex.lvs.ggp.games.flip.FlipState.BoxState;

import java.util.*;

public class Flip implements StateMachine {
    @Override
    public List<Move> getMoves(State s, Player p) {
        FlipState state = (FlipState) s;
        List<Player> players = state.getPlayers();
        Player currentPlayer = state.wTurn?players.get(0):players.get(1);

        List<Move> moves = new ArrayList<>();

        if (p != currentPlayer) {
            // Return the null move
            moves.add(FlipMove.getNullMove(p));
        } else {
            // Return all possible moves (places which are still empty)
            for (int i = 0; i < state.N*state.N; i++) {
                if (state.board[i] == FlipState.BoxState.empty) {
                    FlipMove move = new FlipMove(p, i % state.N, i / state.N);
                    moves.add(move);
                }
            }
        }

        return moves;
    }

    @Override
    public State getNextState(State s, Map<Player, Move> moves) throws IllegalMoveException {
        FlipState state = (FlipState) s.clone();
        List<Player> players = state.getPlayers();
        Player currentPlayer = state.wTurn?players.get(0):players.get(1);

        // Check the moves of all the players. If it is the turn of the player, the move should
        // be on an empty cell. Else, the move should be the null move.
        for (Player p: moves.keySet()) {
            FlipMove m = (FlipMove) moves.get(p);

            if (p == currentPlayer) {
                // It is the currentPlayer's move, so check that it is valid
                if (m.x < 0 | m.x >= state.N | m.y < 0 | m.y >= state.N)
                    throw new IllegalMoveException(state, m);

                int idx = m.y * state.N + m.x;

                if (state.board[idx] != BoxState.empty)
                    throw new IllegalMoveException(state, m);

                // Update board
                state.board[idx] = state.wTurn? BoxState.W: BoxState.B;
            } else {
                if (m.x != -1 | m.y != -1) {
                    throw new IllegalMoveException(state, m);
                }
            }
        }

        // Flip the necessary cells
        performFlip(state);

        state.wTurn = !state.wTurn;

        return state;
    }

    private void performFlip(FlipState state) {
        BoxState[] newBoard = Arrays.copyOf(state.board, state.board.length);

        for (int x = 0; x < state.N; x++) {
            for (int y = 0; y < state.N; y++) {
                BoxState currentState = getCell(state, x, y);
                if (currentState == BoxState.empty) continue;

                BoxState opponentState = (currentState == BoxState.B)?BoxState.W:BoxState.B;

                int numOfOppCells = 0;

                numOfOppCells += (getCell(state, x - 1, y - 1) == opponentState)?1:0;
                numOfOppCells += (getCell(state, x, y - 1) == opponentState)?1:0;
                numOfOppCells += (getCell(state, x + 1, y - 1) == opponentState)?1:0;
                numOfOppCells += (getCell(state, x - 1, y) == opponentState)?1:0;
                numOfOppCells += (getCell(state, x + 1, y) == opponentState)?1:0;
                numOfOppCells += (getCell(state, x - 1, y + 1) == opponentState)?1:0;
                numOfOppCells += (getCell(state, x, y + 1) == opponentState)?1:0;
                numOfOppCells += (getCell(state, x + 1, y + 1) == opponentState)?1:0;

                if (numOfOppCells >= 3) newBoard[y * state.N + x] = opponentState;
            }
        }

        state.board = newBoard;
    }

    private BoxState getCell (FlipState state, int x, int y) {
        int idx = y * state.N + x;
        if (idx < 0 | idx >= state.N * state.N) return BoxState.empty;

        return state.board[idx];
    }

    @Override
    public boolean isTerminal(State s) {
        return Arrays.stream(((FlipState) s).board).noneMatch(o -> o == FlipState.BoxState.empty);
    }

    @Override
    public Map<Player, Integer> getScores(State s) {
        FlipState state = (FlipState) s;
        List<Player> players = s.getPlayers();

        Map<Player, Integer> scores = new HashMap<>();

        int whiteScore = (int) Arrays
                .stream(state.board)
                .filter(o -> o == FlipState.BoxState.W)
                .count();
        int blackScore = (int) Arrays
                .stream(state.board)
                .filter(o -> o == FlipState.BoxState.B)
                .count();

        scores.put(players.get(0), whiteScore);
        scores.put(players.get(1), blackScore);

        return scores;
    }

    @Override
    public State getInitialState(List<Player> players) {
        return getInitialState(players, 5);
    }

    public State getInitialState(List<Player> players, int N) {
        if (players.size() != 2) throw new IllegalArgumentException("Flip requires exactly "
                + "two players. There were " + players.size() + " players provided.");
        FlipState initialState = new FlipState(players, N);
        return initialState;
    }
}
