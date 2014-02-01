package ss.project.server;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;

import ss.project.ProtocolConstants;
import ss.project.server.gui.ServerGUI;

public class Server extends Observable implements Runnable, ProtocolConstants {
	/**
	 * Checks to see if a specific port is available.
	 * 
	 * @param port
	 *            the port to check for availability
	 */
	public static boolean portAvailable(int port) {
		if (port < 1100 || port > 65535) {
			return false;
		}
		ServerSocket ss = null;
		DatagramSocket ds = null;
		try {
			ss = new ServerSocket(port);
			ss.setReuseAddress(true);
			ds = new DatagramSocket(port);
			ds.setReuseAddress(true);
			return true;
		} catch (IOException e) {
		} finally {
			if (ds != null) {
				ds.close();
			}

			if (ss != null) {
				try {
					ss.close();
				} catch (IOException e) {
				}
			}
		}
		return false;
	}

	private ArrayList<ServerGame> gameList = new ArrayList<ServerGame>();
	private ArrayList<ServerPeer> idlePeerList = new ArrayList<ServerPeer>();
	private int peerCounter = 0;
	private ServerSocket serverSocket;

	public Server() {
	}

	public void setPort(int p) {
		try {
			serverSocket = new ServerSocket(p);

		} catch (IOException e) {
			System.out.println("Dit is echt zo jammer!");
		}
		new Thread(this).start();
	}

	public void addPeer(ServerPeer newPeer) {
		if (!idlePeerList.contains(newPeer)) {
			idlePeerList.add(newPeer);
			setChanged();
			notifyObservers(newPeer);
			checkForNewGames();
		}
	}

	public void checkForNewGames() {

		int readyPeerAmount = 0;
		for (ServerPeer peer : idlePeerList) {
			if (peer.isReady()) {
				readyPeerAmount++;
			}
		}
		if (readyPeerAmount >= 2) {
			ArrayList<ServerPeer> gamePeers = new ArrayList<ServerPeer>();

			for (int i = 4; i >= 2 && gamePeers.isEmpty(); i--) {
				for (ServerPeer peer : idlePeerList) {
					if (peer.getMinimumPlayers() <= i && peer.isReady()) {
						gamePeers.add(peer);
					}
				}
				if (gamePeers.size() < i) {
					gamePeers.clear();
				}
			}
			if (!gamePeers.isEmpty()) {
				startNewGame(gamePeers);
			}
		}
	}

	public void startNewGame(ArrayList<ServerPeer> peers) {
		
		for (ServerPeer peer : peers) {
			idlePeerList.remove(peer);
		}
		ServerGUI.log("Game no. " + (gameList.size() + 1) + " started");
		ServerGame game = new ServerGame(peers);
		gameList.add(game);
		setChanged();

		notifyObservers(game);
	}

	@Override
	public void run() {
		try {
			while (true) {
				Socket socket = serverSocket.accept();
				peerCounter++;
				ServerGUI.log("[Client no. " + peerCounter + " has connected]");
				ServerPeer peer = new ServerPeer(this, "peer" + peerCounter,
						socket);
				addPeer(peer);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
