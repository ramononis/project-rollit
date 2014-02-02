package ss.project.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import ss.project.exceptions.IllegalMoveException;

/**
 * Class for maintaining the Rolit game. Programming project Module 2 Based on
 * the Tic Tac Toe game by Theo Ruys en Arend Rensink.
 * 
 *@author Ramon Onis & Tim Blok
 */
public class Game extends Observable {

	// -- Instance variables -----------------------------------------


	/**
	 * The board.
	 */
	//@ private invariant board != null;

	private Board board;
	/**
	 * Indicates whether or not this is a deep copy of a game instance
	 */
	private boolean isCopy = false;

	/**
	 * List of players by Mark.
	 */
	//@ private invariant (board != null && players.size() >= 2);
	private Map<Mark, Player> players = new HashMap<Mark, Player>();
	/**
	 * Mark of the current player.
	 */
	private Mark current;

	// -- Constructors -----------------------------------------------

	/**
	 * Creates a new Rolit game with an 8x8 board and 4 human players.
	 */
	//@ ensures getBoard() != null && getBoard().getDimension() == 8;
	//@ ensures getPlayers().size() == 4;
	public Game() {
		this(8);
	}

	/**
	 * Creates a new Rolit game with an board with <code>d²</code> fields and 4
	 * human players. <code>d</code> should be greater or equal to 4. Otherwise,
	 * the game will have an 8x8 board.
	 * 
	 *@param d
	 *            The dimension of the board.
	 */
	//@ ensures getBoard() != null && getBoard().getDimension() == (d < 4 ? 8 : d);
	//@ ensures getPlayers().size() == 4;
	public Game(int d) {
		this(d, null);
	}

	/**
	 * Creates a new Rolit game with an board with <code>d²</code> fields and
	 * the players in <code>ps</code> <code>d</code> should be greater or equal
	 * to 4. Otherwise, the game will have an 8x8 board.<br>
	 * If <code>ps</code> is <code>null</code> or has less than 2 players the
	 * game will contains 4 human players.<br>
	 * If <code>ps</code> contains more than 4 players, the first 4 players will
	 * be added to the game.
	 * 
	 *@param d
	 *            The dimension of the board.
	 *@param ps
	 *            The list of players
	 */
	//@ ensures getBoard() != null && getBoard().getDimension() == (d < 4 ? 8 : d);
	//@ ensures ps.size() >= 2 ==> getPlayers().values().containsAll(ps);
	//@ ensures ps.size() < 2 ==> getPlayers().size() == 4;
	public Game(int d, ArrayList<Player> ps) {
		
		board = new Board(d < 4 ? 8 : d);
		current = Mark.RED;
		reset(ps);
	}

	// -- Queries ----------------------------------------------------

	/**
	 * Returns the board.
	 */
	//@ ensures \result != null;
	/*@ pure */ public Board getBoard() {
		return board;
	}

	/**
	 * Returns the mark of the player whose turn it is.
	 */
	//@ ensures \result != null && \result != Mark.EMPTY;
	/*@ pure */ public Mark getCurrent() {
		return current;
	}
	/**
	 * Creates a copy of this game instance. The returned game won't
	 * automatically request a move from a player(which normally causes the next
	 * player to take a turn if it's an ComputerPlayer) if a move is taken.
	 * 
	 */
	/*@ pure */ public Game deepCopy() {
		Game game = new Game(board.getDimension());
		game.reset(new ArrayList<Player>(players.values()));
		game.board = board.deepCopy();
		game.current = current;
		game.isCopy = true;
		return game;
	}
	/**
	 * Returns the winning Mark.<br>
	 * If there is a draw or the game isn't over <code>null</code> will be
	 * returned.
	 */
	//@ ensures getBoard().isWinner(\result);
	/*@ pure */ public Mark getWinner() {
		if (board.isWinner(Mark.RED)) {
			return Mark.RED;
		} else if (board.isWinner(Mark.GREEN)) {
			return Mark.GREEN;
		} else if (board.isWinner(Mark.BLUE)) {
			return Mark.BLUE;
		} else if (board.isWinner(Mark.YELLOW)) {
			return Mark.YELLOW;
		} else {
			return null;
		}
	}

	/**
	 * Checks whether the current mark can take a turn on the field on index
	 * <code>i</code>.
	 * 
	 *@param i
	 *            the index of the field where to place the mark
	 *@return true if the move is valid, otherwise false
	 */
	/*@ pure */ public boolean isValidMove(int i) {
		boolean isValid = false;
		if (board.isField(i) && board.isEmptyField(i)
				&& isAdjacentToNonEmptyField(i)) {
			if (canBlock(i)) {
				isValid = true;
			} else if (board.getScore(current) == 0 || !hasBlockPossibility()) {
				isValid = true;
			}
		}
		return isValid;
	}
	
	/**
	 * Returns a list of players by mark.
	 */
	//@ ensures \result.size() >= 2;
	//@ ensures \result.keySet().contains(Mark.RED);
	//@ ensures \result.keySet().contains(Mark.GREEN);
	//@ ensures \result.size() >= 3 ==> \result.keySet().contains(Mark.BLUE);
	//@ ensures \result.size() == 4 ==> \result.keySet().contains(Mark.GREEN);
	/*@ pure */ public Map<Mark, Player> getPlayers() {
		return players;
	}

	// -- Commands ---------------------------------------------------

	/**
	 * Resets the game. <br>
	 * The board is emptied and the players in <code>ps</code> will be added to
	 * the game. <br>
	 * If <code>ps</code> is <code>null</code> or has less than 2 players the
	 * game will contains 4 human players.<br>
	 * If <code>ps</code> contains more than 4 players, the first 4 players will
	 * be added to the game.
	 * 
	 */
	//@ ensures getCurrent() == Mark.RED;
	//@ ensures ps.size() >= 2 ==> getPlayers().values().containsAll(ps);
	//@ ensures ps.size() < 2 ==> getPlayers().size() == 4;
	public void reset(ArrayList<Player> ps) {
		current = Mark.RED;
		board.reset();
		players.clear();
		if (ps == null || ps.size() < 2) {
			players.put(Mark.RED, new HumanPlayer("player1"));
			players.put(Mark.GREEN, new HumanPlayer("player2"));
			players.put(Mark.BLUE, new HumanPlayer("player3"));
			players.put(Mark.YELLOW, new HumanPlayer("player4"));
		} else {
			players.put(Mark.RED, ps.get(0));
			players.put(Mark.GREEN, ps.get(1));
			if (ps.size() > 2) {
				players.put(Mark.BLUE, ps.get(2));
			}
			if (ps.size() > 3) {
				players.put(Mark.YELLOW, ps.get(3));
			}
		}
		setChanged();
		notifyObservers();
	}


	/**
	 * Sets the current mark in field <code>i</code> and takes over other fields
	 * according to the game rules.<br>
	 * If the game isn't over and this isCopy is false, a move of the next
	 * player will be requested.
	 * 
	 *@param i
	 *            the index of the field where to place the mark
	 *@throws IllegalMoveException
	 *             if <code>isValidMove(i)</code> returns <code>false</code>
	 */
	//@ requires 0 <= i & i < getBoard().getDimension() * getBoard().getDimension() && getBoard().isEmptyField(i) && isValidMove(i);
	//@ ensures getBoard().getField(i) == getCurrent();
	public void takeTurn(int i) throws IllegalMoveException {
		if (!isValidMove(i)) {
			throw new IllegalMoveException("Invalid move: " + i);
		}
		board.setField(i, current);
		takeOverBlockedFields(i);
		switch (players.size()) {
		case 2:
			if (current.equals(Mark.GREEN)) {
				current = Mark.RED;
			} else {
				current = current.next();
			}
			break;
		case 3:
			if (current.equals(Mark.BLUE)) {
				current = Mark.RED;
			} else {
				current = current.next();
			}
			break;
		case 4:
			current = current.next();
		}
		if (!isCopy && !board.gameOver()) {
			players.get(current).requestMove(this);
		}
		setChanged();
		notifyObservers();
	}

	/**
	 * Takes over all the blocked fields if the field on index i would be taken
	 * by the current player.<br>
	 * To take over the northwest direction <code>dr</code> and <code>dc</code>
	 * would be -1.<br>
	 * To take over the east directoin <code>dr</code> would be 0 and
	 * <code>dc</code> would be 1.<br>
	 * 
	 *@param i
	 *            the index of the field to take over
	 *@param dr
	 *            the direction component of the rows.
	 *@param dc
	 *            the direction component of the collumns.
	 */
	//@ requires 0 <= i & i < getBoard().getDimension() * getBoard().getDimension() && getBoard().isEmptyField(i) && isValidMove(i);
	private void takeOverBlockedFields(int i) {
		takeOverBlockedFields(i, -1, -1);
		takeOverBlockedFields(i, -1, 0);
		takeOverBlockedFields(i, -1, 1);
		takeOverBlockedFields(i, 0, -1);
		takeOverBlockedFields(i, 0, 1);
		takeOverBlockedFields(i, 1, -1);
		takeOverBlockedFields(i, 1, 0);
		takeOverBlockedFields(i, 1, 1);
	}

	/**
	 * Takes over all the blocked fields if the field on index i would be taken
	 * by the current player in the direction (dr, dc).
	 * 
	 *@param i
	 *@param dr
	 *@param dc
	 */
	//@ requires 0 <= i & i < getBoard().getDimension() * getBoard().getDimension() && getBoard().isEmptyField(i) && isValidMove(i);
	//@ requires -1 <= dr && dr <= 1 && -1 <= dc && dr <= dr;
	private void takeOverBlockedFields(int i, int dr, int dc) {
		if (!canBlock(i, dr, dc)) {
			return;
		}
		boolean foundOwn = false;
		int r = i / board.getDimension();
		int c = i % board.getDimension();
		r += dr;
		c += dc;
		while (!foundOwn) {
			if (board.getField(r, c).equals(current)) {
				foundOwn = true;
			}
			board.setField(r, c, current);
			r += dr;
			c += dc;
		}
		return;
	}

	/**
	 * Sets the current mark in field (r, c) and takes over other fields
	 * according to the game rules.<br>
	 * If the game isn't over and this isCopy is false, a move of the next
	 * player will be requested.
	 * 
	 *@param r
	 *            the row
	 *@param c
	 *            the collumn
	 *@throws IllegalMoveException
	 *             if <code>isValidMove(i)</code> returns <code>false</code>
	 */
	//@ requires 0 <= r & r < getBoard().getDimension();
	//@ requires 0 <= c & c < getBoard().getDimension();
	//@ requires getBoard().isEmptyField(r, c) && isValidMove(r * getBoard().getDimension() + c);
	//@ ensures getBoard().getField(r, c) == getCurrent();
	public void takeTurn(int r, int c) throws IllegalMoveException {
		takeTurn(c + r * board.getDimension());
	}



	/**
	 * Starts this game. Requests an move from the first player.
	 */
	public void start() {
		players.get(current).requestMove(this);
		setChanged();
		notifyObservers();
	}



	// -- isValidMove() helpers ---------------------------
	/**
	 * Checks whether the current mark has a possibility to block other fields .
	 */
	/*@ pure */ private boolean hasBlockPossibility() {
		boolean result = false;
		for (int i = 0; i < board.getDimension() * board.getDimension() && !result; i++) {
			if (board.isEmptyField(i) && canBlock(i)) {
				result = true;
			}
		}
		return result;
	}

	/**
	 * Checks whether a move of the current mark on index i would block other
	 * fields in the direction (dr, dc).<br>
	 * To check the northwest direction <code>dr</code> and <code>dc</code>
	 * would be -1.<br>
	 * To check the east directoin <code>dr</code> would be 0 and
	 * <code>dc</code> would be 1.<br>
	 * 
	 *@param i
	 *            the index of the field to check
	 *@param dr
	 *            the direction component of the rows.
	 *@param dc
	 *            the direction component of the collumns.
	 */
	/*@ pure */ private boolean canBlock(int i, int dr, int dc) {
		boolean result = false;
		int r = i / board.getDimension();
		int c = i % board.getDimension();
		r += dr;
		c += dc;
		if (board.isField(r, c) && !board.isEmptyField(r, c)
				&& !board.getField(r, c).equals(current)) {
			r += dr;
			c += dc;
			while (!result && board.isField(r, c) && !board.isEmptyField(r, c)) {
				if (board.getField(r, c).equals(current)) {
					result = true;
				}
				r += dr;
				c += dc;
			}
		}

		return result;
	}

	/**
	 * Checks whether the move of the current mark at the field on index
	 * <code>i</code> would block other fields.
	 * 
	 *@param i
	 *            the index of the field to check
	 *@return true if this move would block other fields
	 */
	/*@ pure */ private boolean canBlock(int i) {

		boolean result = false;
		if (board.isField(i) && isAdjacentToNonEmptyField(i)) {
			result = canBlock(i, -1, -1) || canBlock(i, -1, 0)
					|| canBlock(i, -1, 1) || canBlock(i, 0, -1)
					|| canBlock(i, 0, 1) || canBlock(i, 1, -1)
					|| canBlock(i, 1, 0) || canBlock(i, 1, 1);
		}
		return result;
	}

	/**
	 * Checks whether the field on index i is adjacent to an non-empty field.
	 * 
	 */
	/*@ pure */ public boolean isAdjacentToNonEmptyField(int i) {
		int c = i / board.getDimension();
		int r = i % board.getDimension();
		boolean isAdjacent = false;
		if (board.isField(--c, r) && !board.isEmptyField(c, r)) {
			isAdjacent = true;
		} else if (board.isField(c, ++r) && !board.isEmptyField(c, r)) {
			isAdjacent = true;
		} else if (board.isField(++c, r) && !board.isEmptyField(c, r)) {
			isAdjacent = true;
		} else if (board.isField(++c, r) && !board.isEmptyField(c, r)) {
			isAdjacent = true;
		} else if (board.isField(c, --r) && !board.isEmptyField(c, r)) {
			isAdjacent = true;
		} else if (board.isField(c, --r) && !board.isEmptyField(c, r)) {
			isAdjacent = true;
		} else if (board.isField(--c, r) && !board.isEmptyField(c, r)) {
			isAdjacent = true;
		} else if (board.isField(--c, r) && !board.isEmptyField(c, r)) {
			isAdjacent = true;
		}
		return isAdjacent;
	}

}
