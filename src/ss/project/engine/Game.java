package ss.project.engine;

import java.util.Observable;

/**
 * Class for maintaining the Rolit game. Programming project Module 2 Based on
 * the Tic Tac Toe game by Theo Ruys en Arend Rensink.
 * 
 * @author Ramon Onis & Tim Blok
 */
public class Game extends Observable {

	// -- Instance variables -----------------------------------------

	/*
	 * @ private invariant board != null;
	 */
	/**
	 * The board.
	 */
	private Board board;

	/**
	 * Index of the current player.
	 */
	private Mark current;
	/**
	 * Amount of players
	 */
	private int playerAmount = 4;

	// -- Constructors -----------------------------------------------
	public Game() {
		this(8);
	}

	public Game(int d) {
		board = new Board(d);
		current = Mark.RED;
	}

	// -- Queries ----------------------------------------------------

	/**
	 * Returns the board.
	 */
	public Board getBoard() {
		return board;
	}

	/**
	 * Returns the mark of the player whose turn it is.
	 */
	public Mark getCurrent() {
		return current;
	}

	// -- Commands ---------------------------------------------------

	/**
	 * Resets the game. <br>
	 * The board is emptied and player[0] becomes the current player.
	 */
	public void reset(int players) {
		current = Mark.RED;
		playerAmount = players;
		board.reset();

		setChanged();
		notifyObservers();
	}

	/*
	 * @ requires 0 <= i & i < Board.dim * Board.dim &
	 * this.getBoard().isEmptyField(i);
	 */
	/**
	 * Sets the current mark in field i. Passes the turn to the other mark.
	 * 
	 * @param i
	 *            the index of the field where to place the mark
	 */
	public void takeTurn(int i) {
		if (isValidMove(i)) {
			board.setField(i, current);
			takeOverBlockedFields(i);
			switch (playerAmount) {
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
			setChanged();
			notifyObservers();
		}
	}

	public void takeOverBlockedFields(int i) {
		takeOverBlockedFields(i, -1, -1);
		takeOverBlockedFields(i, -1, 0);
		takeOverBlockedFields(i, -1, 1);
		takeOverBlockedFields(i, 0, -1);
		takeOverBlockedFields(i, 0, 1);
		takeOverBlockedFields(i, 1, -1);
		takeOverBlockedFields(i, 1, 0);
		takeOverBlockedFields(i, 1, 1);
	}

	public void takeOverBlockedFields(int i, int dr, int dc) {
		if (!canBlock(i, dr, dc)) {
			return;
		}
		boolean foundOwn = false;
		int r = i / board.dim;
		int c = i % board.dim;
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
	 * Checks whether the field on index i is adjacent to an non-empty field.
	 * 
	 */
	public boolean isAdjacentToNonEmptyField(int i) {
		int c = i / board.dim;
		int r = i % board.dim;
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

	/**
	 * Checks whether the move of the current mark at the field on index
	 * <code>i</code> would block other fields.
	 * 
	 * @param i
	 *            the index of the field to check
	 * @return true if this move would block other fields
	 */
	public boolean canBlock(int i) {

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
	 * Checks whether a move of the current mark on index i blocks other fields
	 * in the direction (dr, dc).
	 */
	public boolean canBlock(int i, int dr, int dc) {
		boolean result = false;
		int r = i / board.dim;
		int c = i % board.dim;
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
	 * Checks whether the current mark has a possibility to block other fields .
	 */
	public boolean hasBlockPossibility() {

		boolean result = false;
		for (int i = 0; i < board.dim * board.dim && !result; i++) {
			if (board.isEmptyField(i) && canBlock(i)) {
				result = true;
			}
		}
		return result;
	}

	/**
	 * Checks whether the current mark can take a turn on the field on index
	 * <code>i</code>.
	 * 
	 * @param i
	 *            the index of the field where to place the mark
	 * @return true if the move is valid, otherwise false
	 */
	public boolean isValidMove(int i) {
		boolean isValid = false;
		if (board.isEmptyField(i) && board.isField(i)
				&& isAdjacentToNonEmptyField(i)) {
			if (canBlock(i)) {
				isValid = true;
			} else if (board.getScore(current) == 0 || !hasBlockPossibility()) {
				isValid = true;
			}
		}
		return isValid;
	}

	public Mark getWinner() {
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
}