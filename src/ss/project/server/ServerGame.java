package ss.project.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ss.project.ai.NaiveAi;
import ss.project.ai.SmartAi;
import ss.project.exceptions.IllegalMoveException;
import ss.project.model.Game;
import ss.project.model.Mark;
import ss.project.model.Player;

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
	public void takeTurn(int i) throws IllegalMoveException {
		Player currentPlayer = getPlayers().get(getCurrent());
		super.takeTurn(i);
		for (Player player : peers.keySet()) {
			if (!player.equals(currentPlayer)) {
				ServerPeer peer = peers.get(player);
				peer.sendTurn(i);
			}
		}
		takeAiTurnIfDisconnected();
	}
	public void takeAiTurnIfDisconnected() {
		if (peers.get(getPlayers().get(getCurrent())).isDisconnected()
				&& !getBoard().gameOver()) {
			try {
				takeTurn(new SmartAi().determineMove(this));
			} catch (IllegalMoveException e) {
				try {
					takeTurn(new NaiveAi().determineMove(this));
				} catch (IllegalMoveException e1) {
					//should not be caught. NaiveAi only takes legal moves.
				}
			}
		}
	}

	@Override
	public void start() {
		super.start();
		for (Mark mark : getPlayers().keySet()) {
			Player player = getPlayers().get(mark);
			ServerPeer peer = peers.get(player);
			peer.sendStart();
		}
	}
	/*@ pure */ public Collection<ServerPeer> getPeers() {
		return peers.values();
	}
	private Map<Player, ServerPeer> peers = new HashMap<Player, ServerPeer>();
}
