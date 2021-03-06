package ss.project.ai;
import ss.project.model.Board;
import ss.project.model.Game;
import ss.project.exceptions.IllegalMoveException;

public class SmartAi implements Ai {
	private String name = "Smart";

	public void log(String s) {
		System.out.println(s);
	}

	@Override
	public int determineMove(Game g) {
		Board b = g.getBoard();
		int turn = smartMove(g.deepCopy());
		if (onlyInMidRegion(b.deepCopy())) {
			log("Only in mid region");
			if (canStayInMidRegion(g.deepCopy()) != -1) {
				log("Can stay here");
				turn = canStayInMidRegion(g.deepCopy());
			} else {
				turn = smartMove(g.deepCopy());
			}
		} else if (canGetCorner(g.deepCopy())) {
			turn = cornerMove(g.deepCopy());
		} else if (canGetSafeOuterRingMove(g.deepCopy())) {
			turn = safeOuterRingMove(g.deepCopy());
		}
		return turn;
	}

	private int safeOuterRingMove(Game game) {
		Board board = game.getBoard();
		int dim = board.getDimension();
		int move = -1;
		
		for (int i = 0; i < board.getDimension() * board.getDimension(); i++) {
			if (game.isValidMove(i) && isOnOuterRing(i, dim) && !isNextToCorner(i, dim)) {
				move = i;
			}
		}
		return move;
	}

	private boolean canGetSafeOuterRingMove(Game game) {
		return safeOuterRingMove(game) != -1;
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

	public int smartMove(Game game) {
		Board board = game.getBoard();
		int dim = board.getDimension();
		int move = -1;
		for (int i = 0; i < dim * dim; i++) {
			if (game.isValidMove(i) && !isNextToCorner(i, dim)) {
				boolean foundGoodMove = false;
				Game copy = game.deepCopy();
				try {
					copy.takeTurn(i);
				} catch (IllegalMoveException e) {
					//impossible, should not be caught.
				}
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
					try {
						copy.takeTurn(i);
					} catch (IllegalMoveException e) {
						//impossible, should not be caught.
					}
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
					try {
						copy.takeTurn(i);
					} catch (IllegalMoveException e) {
						//impossible, should not be caught.
					}
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

	public int cornerMove(Game game) {
		Board board = game.getBoard();
		int corner = -1;
		if (game.isValidMove(0)) {
			corner = 0;
		} else if (game.isValidMove(board.getDimension() - 1)) {
			corner = board.getDimension() - 1;
		} else if (game.isValidMove(board.getDimension() * board.getDimension() - 1)) {
			corner = board.getDimension() * board.getDimension() - 1;
		} else if (game.isValidMove(board.getDimension() * board.getDimension() - board.getDimension())) {
			corner = board.getDimension() * board.getDimension() - board.getDimension();
		}
		return corner;
	}

	public boolean canGetCorner(Game game) {
		return cornerMove(game) != -1;
	}
	
	public boolean isFieldInMidRegion(int x, int y) {
		return !(x < 2 || x > 5 || y < 2 || y > 5);
	}

	public boolean onlyInMidRegion(Board board) {
		boolean foundFieldOutsideRegion = false;
		for (int i = 0; i < board.getDimension() * board.getDimension() && !foundFieldOutsideRegion; i++) {
			int x = i % board.getDimension();
			int y = i / board.getDimension();
			if (!board.isEmptyField(i) && !isFieldInMidRegion(x, y)) {
				foundFieldOutsideRegion = true;
			}
		}
		return !foundFieldOutsideRegion;
	}

	public int canStayInMidRegion(Game game) {
		Board board = game.getBoard();
		int move = -1;
		for (int i = 0; i < board.getDimension() * board.getDimension() && move == -1; i++) {
			int x = i % board.getDimension();
			int y = i / board.getDimension();
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
