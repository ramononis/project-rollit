package ss.project.ai;

import ss.project.engine.Game;

public interface Ai {
	public int determineMove(Game game);
	public String getName();
}
