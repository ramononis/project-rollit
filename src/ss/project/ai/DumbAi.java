package ss.project.ai;

import java.util.ArrayList;
import java.util.Random;

import ss.project.engine.Board;
import ss.project.engine.Game;
import ss.project.engine.Mark;
import ss.project.exceptions.IllegalMoveException;

public class DumbAi implements Ai {
	private String name = "Dumb ai";

	@Override
	public int determineMove(Game game) {
		Board board = game.getBoard();
		Mark mark = game.getCurrent();
		int maxScore = -1;
		ArrayList<Integer> maxMoves = new ArrayList<Integer>();
		for (int i = 0; i < board.dim * board.dim; i++) {
			Game copy = game.deepCopy();
			copy.isCopy = true;
			if (copy.isValidMove(i)) {
				try {
					copy.takeTurn(i);
				} catch (IllegalMoveException e) {
					//impossible. should not be catched
				}
				if (copy.getBoard().getScore(mark) > maxScore) {
					maxMoves.clear();
					maxMoves.add(i);
					maxScore = copy.getBoard().getScore(mark);
				} else if (copy.getBoard().getScore(mark) == maxScore) {
					maxMoves.add(i);
				}
			}
		}
		return maxMoves.get(new Random().nextInt(maxMoves.size()));
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
