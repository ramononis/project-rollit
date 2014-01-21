package ss.project.ai;

import ss.project.engine.Board;
import ss.project.engine.Game;
import ss.project.engine.Mark;

public class DumbAi implements Ai {

	@Override
	public int determineMove(Game game) {
		Board board = game.getBoard();
		Mark mark = game.getCurrent();
		int maxScore = -1;
		int maxMove = -1;
		for (int i = 0; i < board.dim * board.dim; i++) {
			Game copy = game.deepCopy();
			if (game.isValidMove(i)) {
				copy.takeTurn(i);
				if (copy.getBoard().getScore(mark) > maxScore) {
					maxMove = i;
					maxScore = copy.getBoard().getScore(mark);
				}
			}
		}
		return maxMove;
	}
}
