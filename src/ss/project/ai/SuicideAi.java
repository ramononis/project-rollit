package ss.project.ai;

import java.util.ArrayList;
import java.util.Random;

import ss.project.engine.Board;
import ss.project.engine.Game;
import ss.project.engine.Mark;
import ss.project.exceptions.IllegalMoveException;

public class SuicideAi implements Ai {
	private String name = "Suicide ai";

	@Override
	public int determineMove(Game game) {
		Board board = game.getBoard();
		Mark mark = game.getCurrent();
		int minScore = board.dim * board.dim + 1;
		ArrayList<Integer> minMoves = new ArrayList<Integer>();
		for (int i = 0; i < board.dim * board.dim; i++) {
			Game copy = game.deepCopy();
			copy.isCopy = true;
			if (copy.isValidMove(i)) {
				try {
					copy.takeTurn(i);
				} catch (IllegalMoveException e) {
					//impossible, should not be catched.
				}
				if (copy.getBoard().getScore(mark) < minScore) {
					minMoves.clear();
					minMoves.add(i);
					minScore = copy.getBoard().getScore(mark);
				} else if (copy.getBoard().getScore(mark) == minScore) {
					minMoves.add(i);
				}
			}
		}
		int move = -1;
		if (!minMoves.isEmpty()) {
			move = new Random().nextInt(minMoves.size());
		}
		return minMoves.get(move);
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
