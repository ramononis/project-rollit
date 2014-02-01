package ss.project.engine;

import ss.project.ai.Ai;
import ss.project.ai.NaiveAi;
import ss.project.exceptions.IllegalMoveException;

public class ComputerPlayer extends Player {
	private Ai ai;
	public ComputerPlayer(Ai newAi) {
		setAi(newAi);
	}

	@Override
	public void requestMove(Game g) {
		try {
			g.takeTurn(ai.determineMove(g));
		} catch (IllegalMoveException e) {
			try {
				g.takeTurn(new NaiveAi().determineMove(g));
			} catch (IllegalMoveException e1) {
				//should not be catched. NaiveAi always takes an legal move.
			}
		}
	}
	public Ai getAi() {
		return ai;
	}
	public void setAi(Ai newAi) {
		ai = newAi;
		setName(ai.getName());
	}

}
