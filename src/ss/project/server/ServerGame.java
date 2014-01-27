package ss.project.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ss.project.engine.Game;
import ss.project.engine.Mark;
import ss.project.engine.Player;

public class ServerGame extends Game {
	public ServerGame(ArrayList<ServerPeer> ps) {
		this(ps, 8);
	}

	public ServerGame(ArrayList<ServerPeer> ps, int d) {
		super(d);
		ArrayList<Player> players = new ArrayList<Player>();
		for (ServerPeer peer : ps) {
			peer.setGame(this);
			Player player = new Player(peer.getName());
			peers.put(player, peer);
			players.add(player);
		}
		reset(players);
	}

	@Override
	public void takeTurn(int i) {
		Player currentPlayer = getPlayers().get(getCurrent());
		super.takeTurn(i);
		for (Player player : peers.keySet()) {
			if (!player.equals(currentPlayer)) {
				ServerPeer peer = peers.get(player);
				peer.sendTurn(i);
			}
		}
	}

	@Override
	public void start() {
		super.start();
		for (Mark mark : getPlayers().keySet()) {
			Player player = getPlayers().get(mark);
			ServerPeer peer = peers.get(player);
			peer.sendStart(peers.size(), mark);
		}
	}

	private Map<Player, ServerPeer> peers = new HashMap<Player, ServerPeer>();
}
