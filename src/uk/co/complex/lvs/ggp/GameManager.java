package uk.co.complex.lvs.ggp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import uk.co.complex.lvs.ggp.forms.GameOutput;
import uk.co.complex.lvs.ggp.games.connectfour.ConnectFour;
import uk.co.complex.lvs.ggp.games.connectfour.ConnectFourHuman;
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
	
	/**
	 * Starts running the given game with the provided players. It asks the players for moves until 
	 * a terminal state is reached. Each player has a certain number of milliseconds to decide
	 * which move it wants to play. If the player fails to provide a move within time or if it
	 * provides an illegal move, then a random move is selected for that player.
	 * @param game The StateMachine which represents the concept of the game
	 * @param players A list of players who will play the game
	 * @param time The number of seconds each player has to return its next move
	 * @param output The GameOutput object to which game information can be send
	 * @return The scores at the end of the game
	 */
	public Map<Player, Integer> play(final StateMachine game, List<Player> players, final int time, GameOutput output) {
		State mState = game.getInitialState(players);
		
		output.print(mState);
		
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
				Thread t = new Thread() {
					public void run() {
						Move m = p.getNextMove(tempState, game, time);
						moves.put(p, m);
					}
				};
				
				threads.put(p, t);
			}
			
			// Start each thread, asking the players for their move
			for (Player p: threads.keySet()) {
				threads.get(p).start();
			}
			
			// Wait for a given time, allowing the players to make moves
			int numOfBlocks = 20;
			for (int i = 0; i < numOfBlocks; i++) {
				// Sleep for a small time
				try {
					TimeUnit.MILLISECONDS.sleep(time/numOfBlocks);
				} catch (InterruptedException e1) {
					throw new RuntimeException("The main thread is not able to sleep");
				}
				
				// Check if all threads have already finished. If so, stop pausing
				boolean allFinished = true;
				for (Player p: threads.keySet()) {
					Thread t = threads.get(p);
					if (t.getState() != Thread.State.TERMINATED) {
						allFinished = false;
					}
				}
				if (allFinished) break;
			}
			
			// Check if all the players have returned a move. If not, then select a random move
			// for them.
			for (Player p: threads.keySet()) {
				Thread t = threads.get(p);
				
				if (t.getState() != Thread.State.TERMINATED) {
					moves.put(p, selectRandomMove(p, mState, game));
					output.log("The player " + p.getName() + " has not responded in time");
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
	 * @param time The number of seconds each player has to return its next move
	 * @return The scores at the end of the game
	 */
	public Map<Player, Integer> play(final StateMachine game, List<Player> players, final int time) {
		// Call the regular GameManager.play method. The GameOutput is the System console.
		return play(game, players, time, new GameOutput(){
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
		players.add(new ConnectFourHuman("Paul"));
		players.add(new FixedDepthPlayer("Lex"));
		StateMachine game = new ConnectFour();
		int time = 3000;
		
		// Start the game
		Map<Player, Integer> scores = man.play(game, players, time);
	}
}
