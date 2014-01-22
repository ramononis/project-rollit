package ss.project.ai;

import java.util.ArrayList;
import java.util.Random;

import ss.project.engine.Board;
import ss.project.engine.Game;
import ss.project.engine.Mark;

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
				copy.takeTurn(i);
				if (copy.getBoard().getScore(mark) > maxScore) {
					maxMoves.clear();
					maxMoves.add(i);
					maxScore = copy.getBoard().getScore(mark);
				} else if (copy.getBoard().getScore(mark) == maxScore) {
					maxMoves.add(i);
				}
			}
		}
		int move = -1;
		if (!maxMoves.isEmpty()) {
			move = new Random().nextInt(maxMoves.size());
		}
		return maxMoves.get(move);
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
