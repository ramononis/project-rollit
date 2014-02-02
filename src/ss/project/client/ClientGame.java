package ss.project.client;

import java.util.ArrayList;

import ss.project.model.Game;
import ss.project.model.Mark;
import ss.project.model.Player;
import ss.project.exceptions.IllegalMoveException;

public class ClientGame extends Game {
	private Client client;

	public Client getClient() {
		return client;
	}

	public ClientGame(Client c, ArrayList<Player> ps) {
		this(c, ps, 8);
	}

	public ClientGame(Client c, ArrayList<Player> ps, int d) {
		super(d, ps);
		client = c;
	}

	@Override
	public void takeTurn(int i) throws IllegalMoveException {
		Mark currentMark = getCurrent();
		super.takeTurn(i);
		if (currentMark.equals(client.getMyMark())) {
			client.sendTurn(i);
		}
	}

	public void takeTurn(int r, int c) throws IllegalMoveException {
		takeTurn(r * getBoard().getDimension() + c);
	}

	public void setMyPlayer(Player player) {
		getPlayers().put(client.getMyMark(), player);
	}
}
