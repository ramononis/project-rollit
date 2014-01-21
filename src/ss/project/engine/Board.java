package ss.project.engine;

/**
 * Game board for the Rolit game. Module 2 programming project.
 * Based on the Tic Tac Toe game board by Theo Ruys en Arend Rensink.
 * 
 * @author Ramon Onis & Tim Blok
 */
public class Board {

	// -- Instance variables -----------------------------------------

	public int dim;
	/*
	 * @ private invariant fields.length == dim*dim; invariant (\forall int i; 0
	 * <= i & i < dim*dim; getField(i) == Mark.EMPTY || getField(i) == Mark.XX
	 * || getField(i) == Mark.OO);
	 */
	/**
	 * The dim by dim fields of the Tic Tac Toe board. See NUMBERING for the
	 * coding of the fields.
	 */
	private Mark[] fields;

	// -- Constructors -----------------------------------------------

	/*
	 * @ ensures (\forall int i; 0 <= i & i < dim * dim; this.getField(i) ==
	 * Mark.EMPTY) && dim == 8;
	 */
	/**
	 * Creates an empty board.
	 */
	public Board() {
		this(8);
	}

	/*
	 * @ ensures (\forall int i; 0 <= i & i < dim * dim; this.getField(i) ==
	 * Mark.EMPTY);
	 */
	/**
	 * Creates an empty board.
	 */
	public Board(int d) {
		dim = d;
		fields = new Mark[dim * dim];
		reset();
	}

	// -- Queries ----------------------------------------------------

	/*
	 * @ ensures \result != this; ensures (\forall int i; 0 <= i & i < dim *
	 * dim; \result.getField(i) == this.getField(i));
	 */
	/**
	 * Creates a deep copy of this field.
	 */
	public Board deepCopy() {
		Board copy = new Board();
		for (int i = 0; i < fields.length; i++) {
			copy.fields[i] = this.fields[i];
		}
		return copy;
	}

	/*
	 * @ requires 0 <= row & row < dim; requires 0 <= col & col < dim;
	 */
	/**
	 * Calculates the index in the linear array of fields from a (row, col)
	 * pair.
	 * 
	 * @return the index belonging to the (row,col)-field
	 */
	public int index(int row, int col) {
		return dim * row + col;
	}

	/*
	 * @ ensures \result == (0 <= ix && ix < dim * dim);
	 */
	/**
	 * Returns true if <code>ix</code> is a valid index of a field on tbe board.
	 * 
	 * @return <code>true</code> if <code>0 <= ix < dim*dim</code>
	 */
	/* @pure */
	public boolean isField(int ix) {
		return (0 <= ix) && (ix < dim * dim);
	}

	/*
	 * @ ensures \result == (0 <= row && row < dim && 0 <= col && col < dim);
	 */
	/**
	 * Returns true of the (row,col) pair refers to a valid field on the board.
	 * 
	 * @return true if <code>0 <= row < dim && 0 <= col < dim</code>
	 */
	/* @pure */
	public boolean isField(int row, int col) {
		return (0 <= row) && (row < dim) && (0 <= col) && (col < dim);
	}

	/*
	 * @ requires this.isField(i); ensures \result == Mark.EMPTY || \result ==
	 * Mark.XX || \result == Mark.OO;
	 */
	/**
	 * Returns the content of the field <code>i</code>.
	 * 
	 * @param i
	 *            the number of the field (see NUMBERING)
	 * @return the mark on the field
	 */
	public Mark getField(int i) {
		return fields[i];
	}

	/*
	 * @ requires this.isField(row,col); ensures \result == Mark.EMPTY ||
	 * \result == Mark.XX || \result == Mark.OO;
	 */

	/**
	 * Returns the content of the field referred to by the (row,col) pair.
	 * 
	 * @param row
	 *            the row of the field
	 * @param col
	 *            the column of the field
	 * @return the mark on the field
	 */
	public Mark getField(int row, int col) {
		return fields[index(row, col)];
	}

	/*
	 * @ requires this.isField(i); ensures \result == (this.getField(i) ==
	 * Mark.EMPTY);
	 */
	/**
	 * Returns true if the field <code>i</code> is empty.
	 * 
	 * @param i
	 *            the index of the field (see NUMBERING)
	 * @return true if the field is empty
	 */
	public boolean isEmptyField(int i) {
		return getField(i) == Mark.EMPTY;
	}

	/*
	 * @ requires this.isField(row,col); ensures \result ==
	 * (this.getField(row,col) == Mark.EMPTY);
	 */
	/**
	 * Returns true if the field referred to by the (row,col) pair it empty.
	 * 
	 * @param row
	 *            the row of the field
	 * @param col
	 *            the column of the field
	 * @return true if the field is empty
	 */
	/* @pure */
	public boolean isEmptyField(int row, int col) {
		return isEmptyField(index(row, col));
	}

	/*
	 * @ ensures \result == (\forall int i; i <= 0 & i < dim * dim;
	 * this.getField(i) != Mark.EMPTY);
	 */
	/**
	 * Tests if the whole board is full.
	 * 
	 * @return true if all fields are occupied
	 */
	/* @pure */
	public boolean isFull() {
		boolean result = true;
		for (int i = 0; i < fields.length; i++) {
			if (isEmptyField(i)) {
				result = false;
			}
		}
		return result;
	}

	/*
	 * @ ensures \result == this.isFull() || this.hasWinner();
	 */
	/**
	 * Returns true if the game is over. The game is over when there is a winner
	 * or the whole board is full.
	 * 
	 * @return true if the game is over
	 */
	/* @pure */
	public boolean gameOver() {
		return isFull();
	}

	/*
	 * @ requires m == Mark.XX | m == Mark.OO; ensures \result == this.hasRow(m)
	 * || this.hasColumn(m) | this.hasDiagonal(m);
	 */
	/**
	 * Checks if the mark <code>m</code> has won. A mark wins if it controls the
	 * most fields,
	 * 
	 * @param m
	 *            the mark of interest
	 * @return <code>true</code> if the mark has won
	 */
	/* @pure */
	public boolean isWinner(Mark m) {
		int[] scores = new int[] {getScore(Mark.RED), getScore(Mark.GREEN),
				getScore(Mark.BLUE), getScore(Mark.YELLOW) };
		int maxCount = 0;
		int maxScore = -1;
		for (int score : scores) {
			if (maxScore < score) {
				maxCount = 1;
				maxScore = score;
			} else if (maxScore == score) {
				maxCount++;
			}

		}
		return isFull() && maxCount == 1 && maxScore == getScore(m);
	}

	/**
	 * Iterates over the fields and counts the amount of fields that belongs to
	 * <code>m</code>.
	 * 
	 * @param m
	 * @return the
	 */
	public int getScore(Mark m) {
		int counter = 0;
		for (Mark mark : fields) {
			if (mark.equals(m)) {
				counter++;
			}
		}
		return counter;

	}

	/*
	 * @ ensures \result == isWinner(Mark.XX) | \result == isWinner(Mark.OO);
	 */
	/**
	 * Returns true if the game has a winner. This is the case when one of the
	 * marks controls at least one row, column or diagonal.
	 * 
	 * @return true if the board has a winner.
	 */
	/* @pure */
	public boolean hasWinner() {
		return isWinner(Mark.RED) || isWinner(Mark.GREEN)
				|| isWinner(Mark.BLUE) || isWinner(Mark.YELLOW);
	}

	// -- Commands ---------------------------------------------------

	/*
	 * @ ensures (\forall int i; 0 <= i & i < dim * dim; this.getField(i) ==
	 * Mark.EMPTY);
	 */
	/**
	 * Empties all fields of this board (i.e., let them refer to the value
	 * Mark.EMPTY).
	 */
	public void reset() {
		for (int i = 0; i < fields.length; i++) {
			setField(i, Mark.EMPTY);
		}
		setField(dim / 2 - 1, dim / 2 - 1, Mark.RED);
		setField(dim / 2 - 1, dim / 2, Mark.YELLOW);
		setField(dim / 2, dim / 2 - 1, Mark.BLUE);
		setField(dim / 2, dim / 2, Mark.GREEN);
	}

	/*
	 * @ requires this.isField(i); ensures this.getField(i) == m;
	 */
	/**
	 * Sets the content of field <code>i</code> to the mark <code>m</code>.
	 * 
	 * @param i
	 *            the field number (see NUMBERING)
	 * @param m
	 *            the mark to be placed
	 */
	public void setField(int i, Mark m) {
		fields[i] = m;
	}

	/*
	 * @ requires this.isField(row,col); ensures this.getField(row,col) == m;
	 */
	/**
	 * Sets the content of the field represented by the (row,col) pair to the
	 * mark <code>m</code>.
	 * 
	 * @param row
	 *            the field's row
	 * @param col
	 *            the field's column
	 * @param m
	 *            the mark to be placed
	 */
	public void setField(int row, int col, Mark m) {
		setField(index(row, col), m);
	}
}
