package uk.co.complex.lvs.ggp.players;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import uk.co.complex.lvs.ggp.IllegalMoveException;
import uk.co.complex.lvs.ggp.Move;
import uk.co.complex.lvs.ggp.Player;
import uk.co.complex.lvs.ggp.State;
import uk.co.complex.lvs.ggp.StateMachine;

/**
 * The MCTSPlayer is a player which uses the Monte Carlo approach to play games. More specifically,
 * it implements the Monte Carlo Tree Search algorithm. It is therefore a non-deterministic player.
 * 
 * The MCTS is a best-first search algorithm. It builds up the game tree by looking at the most
 * promising nodes (exploitation). At the same time, it does not want to narrow its search too
 * much. To achieve this, it also explores nodes which have not been looked at much (exploration).
 * This way the game tree explored in an efficient way, allowing the player to search deeper. 
 * @author Lex van der Stoep
 */
public class MCTSPlayer extends Player {
	// Time variables to assist the player in responding in time to the game manager
	private long startTime;
	private long totTime;
	
	private StateMachine machine;
	private Player opponent;
	
	private Random rnd = new Random();
	
	// The number of milliseconds which should at least be left when responding to the game manager
	private static final long minTimeToRespond = 100;

	public MCTSPlayer(String name) {
		super(name);
	}

	@Override
	public Move getNextMove(State s, StateMachine m, int time) {
		// Set variables to keep track of available time
		startTime = System.currentTimeMillis();
		totTime = time;
		machine = m;
		
		List<Player> players = s.getPlayers();
		if (players.size() != 2) throw new IllegalArgumentException("The MCTS algorithm was "
				+ "implemented for a two-player game.");
		opponent = (players.get(0)==this)?players.get(1):players.get(0);
		
		// If there is only one legal move available, choose that one.
		List<Move> moves = m.getMoves(s, this);
		if (moves.size() == 1) return moves.get(0);
		
		
		Node rootNode = new Node(null, s);
		rootNode.visitCount++;
		
		// Run the MCTS algorithm, expanding the game tree as long as there is time left
		while (getTimeLeft() > minTimeToRespond) {
			Node currentNode = rootNode;
			
			// SELECTION
			while (currentNode.visitCount > 0) {
				currentNode.visitCount++;
				currentNode = select(currentNode);
				if (currentNode.isTerminal) break;
			}
			
			// EXPANSION
			expand(currentNode);
			
			// SIMULATION
			int score = simulate(currentNode);
			
			//BACKPROPAGATION
			currentNode.value = score;
			currentNode = currentNode.parentNode;
			while (currentNode != rootNode) {
				backpropagation(currentNode);
				currentNode = currentNode.parentNode;
			}
		}
		
		// Find the child node of the root with the highest value
		int idx = -1;
		int maxVal = Integer.MIN_VALUE;
		for (int i = 0; i < rootNode.children.size(); i++) {
			int val = rootNode.children.get(i).value;
			if (val > maxVal) {
				idx = i;
				maxVal = val;
			}
		}
		
		// Return the move which leads to the node with the highest score
		return moves.get(idx);
	}
	
	/**
	 * This method represents the selection procedure of the MCTS algorithm. It looks at the child
	 * nodes of the given node and select the 'best' node to explore. Which node is the best is
	 * determined by using the Upper Confidence Bounds applied to Trees(UCT) strategy. The selected
	 * node is the node with the highest value according to the UCT formula.
	 * @param n The current node
	 * @return The 'best' child node to explore
	 */
	private Node select(Node n) {
		final double C = 100.0 * Math.sqrt(2.0); // This is the UCT constant
		
		Node bestNode = null;
		int bestVal = Integer.MIN_VALUE;
		
		// Select the node with the highest value according to the UCB formula
		if (n.children == null) getChildren(n);
		for (Node c: n.children) {
			int val = (int) (c.value + C * Math.sqrt(Math.log(n.visitCount)/c.visitCount));
			if (val > bestVal) {
				bestVal = val;
				bestNode = c;
			}
		}
		
		return bestNode;
	}
	
	/**
	 * This method represents the expansion procedure of the MCTS algorithm. It adds the given node to
	 * the searched game tree.
	 * @param n
	 */
	private void expand(Node n) {
		n.visitCount++;
		getChildren(n);
	}
	
	/**
	 * This method represents the simulation procedure of the MCTS algorithm. It performs a sequence
	 * of random moves from the given state, until it reaches a terminal state.
	 * @param n The node to start simulating from
	 * @return The score of the terminal state
	 */
	private int simulate(Node n) {
		State currentState = n.state;
		
		while (!machine.isTerminal(currentState)) {
			List<Move> playerMoves = machine.getMoves(currentState, this);
			List<Move> opponentMoves = machine.getMoves(currentState, opponent);
			Map<Player, Move> moves = new HashMap<>();
			moves.put(this, getRandomMove(playerMoves));
			moves.put(opponent, getRandomMove(opponentMoves));
			
			// Apply the random moves
			try {
				currentState = machine.getNextState(currentState, moves);
			} catch (IllegalMoveException e) {
				throw new AssertionError("The random moves played should have been valid");
			}
		}
		
		return machine.getScores(currentState).get(this);
	}
	
	/**
	 * This method represents the backpropagation procedure of the MCTS algorithm. It updates the
	 * value of the given node by averaging the value of its child nodes, according to their
	 * weights.
	 * @param n The node to start simulating from
	 */
	private void backpropagation(Node n) {
		double totScore = 0;
		
		// Average the values of the child nodes, with the visit counts as weights
		for (Node c: n.children) {
			totScore += c.value * c.visitCount;
		}
		
		n.value = (int)(totScore/n.visitCount);
	}
	
	private void getChildren(Node n) {
		// Only set the children of the node n if that node does not already have children and it
		// is not a terminal node.
		if (n.children == null & !n.isTerminal) {
			List<State> childStates = getNextStates(n.state);
			n.children = new ArrayList<>(childStates.size());
			for (State s : childStates) {
				n.children.add(new Node(n, s));
			}
		}
	}
	
	private Move getRandomMove(List<Move> moves) {
		return moves.get(rnd.nextInt(moves.size()));
	}
	
	private long getTimeLeft() {
		return (startTime + totTime - System.currentTimeMillis());
	}
	
	/**
	 * Calculates which states are reachable within one move from the given state
	 * @param s State from which to find the next possible states
	 * @return The possible next states
	 */
	private List<State> getNextStates(State s) {
		List<State> nextStates = new ArrayList<>();
		
		List<Move> thisMoves = machine.getMoves(s, this);
		List<Move> opponentMoves = machine.getMoves(s,  opponent);
		
		for (Move a: thisMoves) {
			for (Move b: opponentMoves) {
				Map<Player, Move> moves = new HashMap<>();
				moves.put(this, a);
				moves.put(opponent, b);
				
				try {
					nextStates.add(machine.getNextState(s, moves));
				} catch (IllegalMoveException e) {
					throw new AssertionError("The moves should be valid, as they were generated by"
							+ " the state machine");
				}
			}
		}
		
		return nextStates;
	}
	
	
	/**
	 * This class represents a node in the game tree. It is used in the MCTS algorithm to refer to
	 * states as nodes.
	 * corresponding game state.
	 */
	private class Node {
		Node parentNode;
		List<Node> children;
		boolean isTerminal;
		
		int visitCount = 0;
		int value;
		
		State state;
		
		public Node(Node parent, State s) {
			parentNode = parent;
			state = s;
			isTerminal = machine.isTerminal(state);
			
			// If this node is the leaf node, then its value will be this player's score for the
			// node's game state.
			if (isTerminal) {
				Map<Player, Integer> scores = machine.getScores(state);
				value = scores.get(MCTSPlayer.this);
			}
		}
	}
}
