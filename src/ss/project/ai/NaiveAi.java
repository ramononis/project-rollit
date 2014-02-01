package ss.project.ai;

import java.util.ArrayList;
import java.util.Random;

import ss.project.engine.Board;
import ss.project.engine.Game;

public class NaiveAi implements Ai {
	private String name = "Naive ai";

	@Override
	public int determineMove(Game game) {
		ArrayList<Integer> possMoves = new ArrayList<Integer>();
		Board board = game.getBoard();
		for (int i = 0; i < board.dim * board.dim; i++) {
			if (game.isValidMove(i)) {
				possMoves.add(i);
			}
		}
		return possMoves.get(new Random().nextInt(possMoves.size()));
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
