package ss.project.client;

import java.util.ArrayList;

import ss.project.engine.Game;
import ss.project.engine.Player;

public class ClientGame extends Game {
	private Client client;

	public ClientGame(Client c, ArrayList<Player> ps) {
		this(c, ps, 8);
	}

	public ClientGame(Client c, ArrayList<Player> ps, int d) {
		super(d, ps);
		client = c;
	}

	@Override
	public void takeTurn(int i) {
		if (isValidMove(i)) {
			super.takeTurn(i);
			client.sendTurn(i);
		}
	}
	public void setMyPlayer(Player player) {
		getPlayers().put(client.getMyMark(), player);
	}
}
