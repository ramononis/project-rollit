package ss.project.server;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;

public class Server extends Observable implements Runnable {
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
					/* should not be thrown */
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
		idlePeerList.add(newPeer);
		System.out.println("Peer no. " + peerCounter + " connected");
		if (idlePeerList.size() >= 2) {
			ArrayList<ServerPeer> gamePeers = new ArrayList<ServerPeer>();
			for (ServerPeer peer : idlePeerList) {
				if (peer.getMinimumPlayers() <= idlePeerList.size()) {
					gamePeers.add(peer);
				}
			}
			if (gamePeers.size() >= 2) {
				startNewGame(gamePeers);
			}
		}
	}

	public void startNewGame(ArrayList<ServerPeer> peers) {
		for (ServerPeer peer : peers) {
			idlePeerList.remove(peer);
		}
		System.out.println("Game no. " + gameList.size() + " started");
		ServerGame game = new ServerGame(peers);
		gameList.add(game);
	}

	@Override
	public void run() {
		try {
			while (true) {
				Socket socket = serverSocket.accept();
				peerCounter++;
				System.out.println("[Client no. " + peerCounter
						+ " has connected]");
				ServerPeer peer = new ServerPeer("peer" + peerCounter, socket);
				addPeer(peer);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
