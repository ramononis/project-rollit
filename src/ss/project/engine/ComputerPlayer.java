package ss.project.engine;

import ss.project.ai.Ai;

public class ComputerPlayer extends Player {
	private Ai ai;
	public ComputerPlayer(Ai newAi) {
		setAi(newAi);
	}

	@Override
	public void requestMove(Game g) {
		g.takeTurn(ai.determineMove(g));
	}
	public Ai getAi() {
		return ai;
	}
	public void setAi(Ai newAi) {
		ai = newAi;
		setName(ai.getName());
	}

}
