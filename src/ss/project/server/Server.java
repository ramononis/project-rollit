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
	 *@param port
	 *            the port to check for availability
	 */
	/*@ pure */ public static boolean portAvailable(int port) {
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

	/**
	 * A list of all the games that have been started.
	 */
	//@ private invariant gameList != null;
	private ArrayList<ServerGame> gameList = new ArrayList<ServerGame>();

	/**
	 * A list of all the connected peers that arn't in a game.
	 */
	//@ private invariant idlePeerList != null;
	private ArrayList<ServerPeer> idlePeerList = new ArrayList<ServerPeer>();
	/**
	 * Counts how many peers have connected.
	 */
	//@ private invariant peerCounter >= 0 && peerCounter >= idlePeerList.size();
	private int peerCounter = 0;
	/**
	 * The servers server socket.
	 */
	private ServerSocket serverSocket;

	/**
	 * Makes a new server. Doesn't actually do anything. A socket won't be made
	 * until a port has been set.
	 */
	public Server() {
	}

	/**
	 * Returns the port of the server socket.
	 */
	/*@ pure */ public int getPort() {
		return serverSocket == null ? -1 : serverSocket.getLocalPort();
	}

	/**
	 * Makes a new ServerSocket using <code>p</code> as port.
	 * 
	 *@param p
	 *            the port to which the server listens.
	 *@throws IOException
	 *             if the port is already in use
	 */
	//@ ensures portAvailable(p) ==> getPort() == p;
	public void setPort(int p) throws IOException {
		serverSocket = new ServerSocket(p);
		new Thread(this).start();
	}

	/**
	 * Adds <code>newPeer</code> to idlePeerList if it doesn't already contain
	 * <code>newPeer</code>. Calls checkForNewGames().
	 */
	public void addPeer(ServerPeer newPeer) {
		if (!idlePeerList.contains(newPeer)) {
			idlePeerList.add(newPeer);
			setChanged();
			notifyObservers(newPeer);
			checkForNewGames();
		}
	}

	/**
	 * Checks if this name is already in use.
	 * 
	 *@param s
	 *            the name to check
	 */
	/*@ pure */ public boolean nameFree(String s) {
		boolean foundMatch = false;
		for (ServerPeer peer : idlePeerList) {
			if (peer.getName().equals(s)) {
				foundMatch = true;
			}
		}
		return !foundMatch;
	}

	/**
	 * Checks if a new game can be started while respecting the minimal player
	 * amount of the idle peers.
	 */
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
	/**
	 * Starts a new ServerGame with <code>peers</code>.
	 *@param peers the list of server peers the game will have.
	 */
	public void startNewGame(ArrayList<ServerPeer> peers) {

		for (ServerPeer peer : peers) {
			idlePeerList.remove(peer);
		}
		ServerGUI.log("Game no. " + (gameList.size() + 1) + " started");
		ServerGame game = new ServerGame(peers);
		gameList.add(game);
		setChanged();
		notifyObservers(game);
		game.start();
	}

	/**
	 * Accepts all new connections.
	 */
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
