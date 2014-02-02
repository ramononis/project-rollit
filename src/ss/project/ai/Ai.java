package ss.project.ai;

import ss.project.model.Game;

public interface Ai {
	public int determineMove(Game game);
	public String getName();
}
