package uk.co.complex.lvs.ggp;

import java.util.*;
import java.util.concurrent.TimeUnit;

import uk.co.complex.lvs.ggp.forms.GameOutput;
import uk.co.complex.lvs.ggp.games.connectfour.ConnectFour;
import uk.co.complex.lvs.ggp.games.connectfour.ConnectFourHuman;
import uk.co.complex.lvs.ggp.games.flip.Flip;
import uk.co.complex.lvs.ggp.games.flip.FlipHuman;
import uk.co.complex.lvs.ggp.games.tictactoe.TicTacToe;
import uk.co.complex.lvs.ggp.games.tictactoe.TicTacToeHuman;
import uk.co.complex.lvs.ggp.players.FixedDepthPlayer;
import uk.co.complex.lvs.ggp.players.MCTSPlayer;
import uk.co.complex.lvs.ggp.players.MinimaxPlayer;
import uk.co.complex.lvs.ggp.players.RandomPlayer;
import uk.co.complex.lvs.ggp.players.VariableDepthPlayer;

/**
 * The GameManager is responsible for managing a game played by one or more players. It keeps track
 * of the game state and asks the players for their moves each turn. Once a game is finished, it
 * provides the scores of all the players.
 * @author Lex van der Stoep
 */
public class GameManager {
	private final Random rnd = new Random();
	private static final int CHECK_TIME = 200; // check for players' responses every 200ms
	
	/**
	 * Starts running the given game with the provided players. It asks the players for moves until 
	 * a terminal state is reached. Each player has a certain number of milliseconds to decide
	 * which move it wants to play. If the player fails to provide a move within time or if it
	 * provides an illegal move, then a random move is selected for that player.
	 * @param game The StateMachine which represents the concept of the game
	 * @param players A list of players who will play the game
	 * @param times The number of seconds each player has to return its next move
	 * @param output The GameOutput object to which game information can be send
	 * @return The scores at the end of the game
	 */
	public Map<Player, Integer> play(final StateMachine game, List<Player> players,
									 Map<Player, Integer> times, GameOutput output) {
		if (!times.keySet().containsAll(players)) {
			throw new IllegalArgumentException("Not each player has been assigned a time");
		}

		State mState = game.getInitialState(players);

		output.print(mState);

		int minTime = times.values().stream().min(Comparator.naturalOrder()).get();
		int maxTime = times.values().stream().max(Comparator.naturalOrder()).get();
		int checkInterval = Math.min(minTime, CHECK_TIME);
		
		// Run the game as long as the state is not terminal.
		while (!game.isTerminal(mState)) {
			// Ask each player for a move
			final Map<Player, Move> moves = new HashMap<>();
			
			// The threads allow all the players to simultaneously make their decisions.
			Map<Player, Thread> threads = new HashMap<>();
			final State tempState = mState.clone();
			
			// Create a thread for each player
			for (final Player p: players) {
				// Create a new thread which asks for the move
				Thread t = new Thread(() -> {
                    Move m = p.getNextMove(tempState, game, times.get(p));
                    moves.put(p, m);
                });
				t.setDaemon(true);
				threads.put(p, t);
			}
			
			// Start each thread, asking the players for their move
			threads.forEach((player,thread)->thread.start());

			// Wait for a given time, allowing the players to make moves
			int totWaitedTime = 0;
			while(totWaitedTime < maxTime) {
				// Sleep for a small time
				try {
					TimeUnit.MILLISECONDS.sleep(checkInterval);
					totWaitedTime += checkInterval;
				} catch (InterruptedException e1) {
					throw new RuntimeException("The main thread is not able to sleep");
				}
				
				// Check if all threads have already finished. If so, stop pausing
				boolean allFinished = threads.values().stream()
						.allMatch(thread -> thread.getState()== Thread.State.TERMINATED);
				if (allFinished) break;

				for (Player p: players) {
					int timetoRespond = times.get(p);

					if (totWaitedTime > timetoRespond) {
						threads.get(p).interrupt();
					}
				}
			}
			
			// Check if all the players have returned a move. If not, then select a random move
			// for them.
			for (Player p: threads.keySet()) {
				Thread t = threads.get(p);
				
				if (t.getState() != Thread.State.TERMINATED) {
					t.interrupt();
					moves.put(p, selectRandomMove(p, mState, game));
					output.log("The player " + p.getName() + " did not respond in time");
				}
			}
			
			// Get the next game state. If the provided move by a player is invalid, then select
			// a random move for that player.
			boolean validNextState = false;
			while (!validNextState) {
				try {
					mState = game.getNextState(mState, moves);
					validNextState = true;
				} catch (IllegalMoveException e) {
					// Get the player who provided an illegal move
					Player p = e.getMove().getPlayer();

					// Update the move that the player provided to be the random move
					moves.put(p, selectRandomMove(p, mState, game));
					output.log("The player " + p.getName() + " provided an illegal move");
				}
			}
			
			output.print(mState);
		}
		
		Map<Player, Integer> scores = game.getScores(mState);

		// Print the scores
		for (Player p : scores.keySet()) {
			output.log("Player " + p + " scored " + scores.get(p) + " points");
		}
		
		return game.getScores(mState);
	}
	
	/**
	 * Starts running the given game with the provided players. It asks the players for moves until 
	 * a terminal state is reached. Each player has a certain number of milliseconds to decide
	 * which move it wants to play. If the player fails to provide a move within time or if it
	 * provides an illegal move, then a random move is selected for that player.
	 * @param game The StateMachine which represents the concept of the game
	 * @param players A list of players who will play the game
	 * @param times The number of seconds each player has to return its next move
	 * @return The scores at the end of the game
	 */
	public Map<Player, Integer> play(final StateMachine game, List<Player> players,
									 Map<Player, Integer> times) {
		// Call the regular GameManager.play method. The GameOutput is the System console.
		return play(game, players, times, new GameOutput(){
			@Override
			public void print(Object message) {
				System.out.println(message);
			}
			@Override
			public void log(Object message) {
				System.out.println(message);
			}
		});
	}
	
	private Move selectRandomMove(Player p, State state, StateMachine game) {
		// Get all moves available to Player p
		List<Move> possibleMoves = game.getMoves(state, p);

		// Pick a random move
		Move rndMove = possibleMoves.get((rnd).nextInt(possibleMoves.size()));
		
		return rndMove;
	}
	
	public static void main(String[] args) {
		// Initialise game parameters
		GameManager man = new GameManager();
		List<Player> players = new ArrayList<>(2);
		Player hennes = new ConnectFourHuman("Hennes");
		Player lex = new VariableDepthPlayer("Lex");
		players.add(hennes);
		players.add(lex);
		StateMachine game = new ConnectFour();
		Map<Player, Integer> times = new HashMap<>();
		times.put(hennes, 100_000);
		times.put(lex, 10_000);
		
		// Start the game
		Map<Player, Integer> scores = man.play(game, players, times);
	}
}
