package ss.project.server;

import java.util.ArrayList;
import java.util.Map;

import ss.project.engine.Game;
import ss.project.engine.Player;

public class ServerGame extends Game {
	public ServerGame(ArrayList<ServerPeer> ps) {
		this(ps, 8);
	}

	public ServerGame(ArrayList<ServerPeer> ps, int d) {
		super(d);
		ArrayList<Player> players = new ArrayList<Player>();
		for (ServerPeer peer : ps) {
			Player player = new Player(peer.getName());
			peers.put(player, peer);
			players.add(player);
		}
		reset(players);
	}

	public Map<Player, ServerPeer> peers;
}
