package ss.project.ai;

import java.util.ArrayList;
import java.util.Random;

import ss.project.engine.Board;
import ss.project.engine.Game;
import ss.project.engine.Mark;

public class NaiveAi implements Ai {

	@Override
	public int determineMove(Game game) {
		ArrayList<Integer> possMoves = new ArrayList<Integer>();
		Board board = game.getBoard();
		Mark mark = game.getCurrent();
		for (int i = 0; i < board.dim * board.dim; i++) {
			if (game.isValidMove(i)) {
				possMoves.add(i);
			}
		}
		int move = -1;
		if (!possMoves.isEmpty()) {
			move = new Random().nextInt(possMoves.size());
		}
		return move;
	}

}
