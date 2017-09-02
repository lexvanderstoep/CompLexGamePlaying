package uk.co.complex.lvs.ggp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.complex.lvs.ggp.games.TicTacToe.TicTacToe;
import uk.co.complex.lvs.ggp.players.RandomPlayer;

public class GameManager {
	
	/**
	 * Starts running the given game with the provide players. It asks the players for moves until 
	 * a terminal state is reached.
	 * @param game The StateMachine which represents the concept of the game
	 * @param players A list of players who will play the game
	 * @param verbose If true, the program will print the state of the game.
	 * @return The scores at the end of the game
	 */
	public Map<Player, Integer> play(StateMachine game, List<Player> players, boolean verbose) {
		State mState = game.getInitialState(players);
		
		if (verbose) System.out.println(mState);
		
		// Run the game as long as the state is not terminal.
		while (!game.isTerminal(mState)) {
			// Ask each player for a move
			Map<Player, Move> moves = new HashMap<>(players.size());
			for (Player p: players) {
				Move m = p.getNextMove(mState, game);
				moves.put(p, m);
			}
			
			try {
				mState = game.getNextState(mState, moves);
			} catch (IllegalMoveException e) {
				System.out.println("Illegal move by: " + e.getMove().getPlayer());
				return null;
			}
			
			if (verbose) System.out.println(mState);
		}
		
		return game.getScores(mState);
	}
	
	/**
	 * Starts running the given game with the provide players. It asks the players for moves until 
	 * a terminal state is reached.
	 * @param game The StateMachine which represents the concept of the game
	 * @param players A list of players who will play the game
	 * @return The scores at the end of the game
	 */
	public Map<Player, Integer> play(StateMachine game, List<Player> players) {
		return play(game, players, false);
	}
	
	public static void main(String[] args) {
		GameManager man = new GameManager();
		List<Player> players = new ArrayList<>(2);
		players.add(new RandomPlayer("Random 1"));
		players.add(new RandomPlayer("Random 2"));
		StateMachine ttt = new TicTacToe();
		
		Map<Player, Integer> scores = man.play(ttt, players, true);
		
		for (Player p: scores.keySet()) {
			System.out.println("Player " + p + " scored " + scores.get(p) + " points");
		}
	}
}
