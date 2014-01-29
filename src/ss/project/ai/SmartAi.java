package ss.project.ai;

import javax.swing.JOptionPane;

import ss.project.engine.Board;
import ss.project.engine.Game;

public class SmartAi implements Ai {
	private String name = "Smart ai";

	public void log(String s) {
		System.out.println(s);
	}

	@Override
	public int determineMove(Game g) {
		//JOptionPane.showMessageDialog(null, "trol");
		Board b = g.getBoard();
		int turn = firstOutSideMidRegion(g.deepCopy());
		if (onlyInMidRegion(b.deepCopy())) {
			log("Only in mid region");
			if (canStayInMidRegion(g.deepCopy()) != -1) {
				log("Can stay here");
				turn = canStayInMidRegion(g.deepCopy());
			} else {
				turn = firstOutSideMidRegion(g.deepCopy());
			}
		} else if (canGetCorner(g.deepCopy()) != -1) {
			turn = canGetCorner(g.deepCopy());
		} else if (getSafeOuterRing(g.deepCopy()) != -1) {
			turn = getSafeOuterRing(g.deepCopy());
		}
		return turn;
	}

	private int getSafeOuterRing(Game game) {
		Board board = game.getBoard();
		int dim = board.dim;
		int move = -1;
		
		for (int i = 0; i < board.dim * board.dim; i++) {
			if (game.isValidMove(i) && isOnOuterRing(i, dim) && !isNextToCorner(i, dim)) {
				move = i;
			}
		}
		return move;
	}

	private int firstOuterRingMove(Game game) {
		Board board = game.getBoard();
		int dim = board.dim;
		int move = firstOutSideMidRegion(game);
		for (int i = 0; i < dim * dim; i++) {
			if (game.isValidMove(i) && isOnOuterRing(i, dim)
					&& !isNextToCorner(i, dim)) {
				move = i;
			}
		}
		return move;
	}

	private boolean outerRingEmpty(Board board) {
		boolean foundOuterField = false;
		for (int i = 0; i < board.dim * board.dim; i++) {
			if (isOnOuterRing(i, board.dim) && !board.isEmptyField(i)) {
				foundOuterField = true;
			}
		}
		return !foundOuterField;
	}

	public boolean isNextToCorner(int i, int dim) {
		
		int x = i % dim;
		int y = i / dim;
		int dx = Math.min(dim - x - 1, x);
		int dy = Math.min(dim - y - 1, y);
		return !isCorner(i, dim) && dy <= 1 && dx <= 1;
	}

	public boolean isCorner(int i, int dim) {
		int x = i % dim;
		int y = i / dim;
		int dx = Math.min(dim - x - 1, x);
		int dy = Math.min(dim - y - 1, y);
		return dy == 0 && dx == 0;
	}

	public boolean isOnOuterRing(int i, int dim) {
		int x = i % dim;
		int y = i / dim;
		int dx = Math.min(dim - x - 1, x);
		int dy = Math.min(dim - y - 1, y);
		return dy == 0 || dx == 0;
	}

	public int firstOutSideMidRegion(Game game) {
		Board board = game.getBoard();
		int dim = board.dim;
		int move = -1;
		for (int i = 0; i < dim * dim; i++) {
			if (game.isValidMove(i) && !isNextToCorner(i, dim)) {
				boolean foundGoodMove = false;
				Game copy = game.deepCopy();
				copy.takeTurn(i);
				for (int j = 0; j < dim * dim && !foundGoodMove; j++) {
					if (copy.isValidMove(j) && isOnOuterRing(i, dim) && !isNextToCorner(i, dim)) {
						foundGoodMove = true;
					}
				}
				if (!foundGoodMove) {
					move = i;
				}
			}
		}
		if (move == -1) {
			for (int i = 0; i < dim * dim; i++) {
				if (game.isValidMove(i) && !diagNextToCorner(i, dim)) {
					boolean foundGoodMove = false;
					Game copy = game.deepCopy();
					copy.takeTurn(i);
					for (int j = 0; j < dim * dim && !foundGoodMove; j++) {
						if (copy.isValidMove(j) && isCorner(j, dim)) {
							foundGoodMove = true;
						}
					}
					if (!foundGoodMove) {
						move = i;
					}
				}
			}
		}
		if (move == -1) {
			for (int i = 0; i < dim * dim; i++) {
				if (game.isValidMove(i)) {
					boolean foundGoodMove = false;
					Game copy = game.deepCopy();
					copy.takeTurn(i);
					for (int j = 0; j < dim * dim && !foundGoodMove; j++) {
						if (copy.isValidMove(j) && isCorner(j, dim)) {
							foundGoodMove = true;
						}
					}
					if (!foundGoodMove) {
						move = i;
					}
				}
			}
		}
		if (move == -1) {
			move = new DumbAi().determineMove(game);
		}
		return move;

	}

	private boolean diagNextToCorner(int i, int dim) {
		int x = i % dim;
		int y = i / dim;
		int dx = Math.min(dim - x - 1, x);
		int dy = Math.min(dim - y - 1, y);
		return !isCorner(i, dim) && dy == 1 && dx == 1;
	}

	public int canGetCorner(Game game) {
		Board board = game.getBoard();
		int corner = -1;
		if (game.isValidMove(0)) {
			corner = 0;
		} else if (game.isValidMove(board.dim - 1)) {
			corner = board.dim - 1;
		} else if (game.isValidMove(board.dim * board.dim - 1)) {
			corner = board.dim * board.dim - 1;
		} else if (game.isValidMove(board.dim * board.dim - board.dim)) {
			corner = board.dim * board.dim - board.dim;
		}
		return corner;
	}

	public boolean isFieldInMidRegion(int x, int y) {
		return !(x < 2 || x > 5 || y < 2 || y > 5);
	}

	public boolean onlyInMidRegion(Board board) {
		boolean foundFieldOutsideRegion = false;
		for (int i = 0; i < board.dim * board.dim && !foundFieldOutsideRegion; i++) {
			int x = i % board.dim;
			int y = i / board.dim;
			if (!board.isEmptyField(i) && !isFieldInMidRegion(x, y)) {
				foundFieldOutsideRegion = true;
			}
		}
		return !foundFieldOutsideRegion;
	}

	public int canStayInMidRegion(Game game) {
		Board board = game.getBoard();
		int move = -1;
		for (int i = 0; i < board.dim * board.dim && move == -1; i++) {
			int x = i % board.dim;
			int y = i / board.dim;
			if (game.isValidMove(i) && isFieldInMidRegion(x, y)) {
				move = i;
			}
		}
		return move;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String n) {
		if (n != null) {
			name = n;
		}
	}
}
