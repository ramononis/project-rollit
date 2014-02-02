package ss.project.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import ss.project.ProtocolConstants;
import ss.project.model.Mark;
import ss.project.model.Player;
import ss.project.exceptions.IllegalMoveException;
import ss.project.server.gui.ServerGUI;

public class ServerPeer implements Runnable, ProtocolConstants {
	/**
	 * The name of this peer.
	 */
	//@ private invariant name != null;
	private String name;
	/**
	 * The socket of this peer.
	 */
	//@ private invariant sock != null;
	private Socket sock;
	/**
	 * Reader that will recieve messages from the client.
	 */
	//@ private invariant in != null;
	private BufferedReader in;
	/**
	 * Writer that will send messages to the client.
	 */
	//@ private invariant out != null;
	private BufferedWriter out;
	/**
	 * The minimal amount of players this peer wants to have in a game.
	 */
	//@ private invariant minimumPlayers == -1 || (2 <= minimumPlayers && minimumPlayers <= 4);
	private int minimumPlayers = -1;
	/**
	 * If not null, the peer is playing in this game.
	 */
	//@ private invariant game != null;
	private ServerGame game;
	/**
	 * The main server instance.
	 */
	//@ private invariant server != null;
	private Server server;
	/**
	 * Indicates whether or not this client is disconnected.<br>
	 * A client is disconnected if an IOException occurs in this class or if the
	 * client has been kicked.
	 */
	private boolean isDisconnected = false;

	/**
	 * Constructor. creates a peer object based in the given parameters. The
	 * name should not contain spaces.
	 * 
	 *@param svr
	 *            the server
	 *@param nameArg
	 *            the name of the client
	 *@param sockArg
	 *            the socket
	 *@throws IOException
	 *             if the io streams fail to connect.
	 */
	//@ requires svr != null && nameArg != null && nameArg.length() > 0 && !nameArg.contains(" ") && sockArg != null;
	public ServerPeer(Server svr, String nameArg, Socket sockArg)
			throws IOException {
		if(nameArg.contains(" ")) {
			throw new IllegalArgumentException("nameArg must not contain spaces");
		}
		name = nameArg;
		sock = sockArg;
		server = svr;
		in = new BufferedReader(new InputStreamReader(sockArg.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(
				sockArg.getOutputStream()));
		new Thread(this).start();
	}

	/**
	 * Sends welcome message to peer which indicates that it is ready to join a
	 * game.
	 */
	private void sendWelcome() {
		if (!isDisconnected) {
			try {
				out.write(WELCOME + '\n');
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Closes the connection, the sockets will be terminated.
	 */
	public void shutDown() {
		try {
			in.close();
			out.close();
			sock.close();
			System.out.println("Exited.");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * returns name of the peer object.
	 */
	//@ ensures \result != null && \result.contains(" ");
	/*@ pure */ public String getName() {
		return name;
	}

	/**
	 * Returns the minimal amount of players in a game.
	 */
	//@ ensures \result == -1 || (2 <= \result && \result <= 4);
	/*@ pure */ public int getMinimumPlayers() {
		return minimumPlayers;
	}

	/**
	 * Sets the minimal amount of players in a game.
	 */
	//@ ensures (minimum == -1 || (2 <= minimum && minimum <= 4)) ==> getMinimumPlayers() == minimum;
	//@ ensures !(minimum == -1 || (2 <= minimum && minimum <= 4)) ==> getMinimumPlayers() == \old(getMinimumPlayers());
	public void setMinimumPlayers(int minimum) {
		minimumPlayers = minimum;
	}

	/**
	 * Sends the start message to the client along with the player names.
	 */
	public void sendStart() {

		if (!isDisconnected) {
			try {
				String line = START_GAME.replaceAll(" ", "");
				Player player = null;
				Mark mark = Mark.RED;
				boolean end = false;
				while ((player = game.getPlayers().get(mark)) != null && !end) {
					line += " " + player.getName();
					mark = mark.next();
					if (mark == Mark.RED) {
						end = true;
					}
				}
				out.write(line + '\n');
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Sends the coordinates of a turn that has been taken.
	 */
	public void sendTurn(int i) {
		if (!isDisconnected) {
			int dim = game.getBoard().getDimension();
			try {
				out.write(UPDATE_GUI + name + " " + (i / dim) + " " + (i % dim)
						+ "\n");
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 *@return the game
	 */
	//@ ensures \result == null || getGame().getPeers().contains(this);
	/*@ pure */ public ServerGame getGame() {
		return game;
	}

	/**
	 *@param game
	 *            the game to set
	 */
	//@ ensures g == null ==> getGame() == \old(getGame());
	//@ ensures g != null ==> getGame() == g;
	public void setGame(ServerGame g) {
		if(game != null) {
			game = g;
		}
	}

	/**
	 * Reads from input stream.
	 */
	@Override
	public void run() {
		try {
			while (true) {
				String line = in.readLine();
				ServerGUI.log("[" + name + "] " + line);
				if (line.contains(LOGIN_GAME)) {
					String peerName = line.replaceAll(LOGIN_GAME, "");
					if (!server.nameFree(peerName)) {
						sendKick("name already in use");
					}
					name = peerName;
					sendWelcome();
				} else if (line.contains(JOIN_GAME)) {
					minimumPlayers = Integer.parseInt(line.replaceAll(
							JOIN_GAME, ""));
					server.addPeer(this);
					server.checkForNewGames();
				} else if (line.contains(UPDATE_GUI)) {
					String line2 = line.replaceAll(UPDATE_GUI, "");
					String[] line2nstuff = line2.split(" ");
					int x = Integer.parseInt(line2nstuff[1]);
					int y = Integer.parseInt(line2nstuff[2]);
					game.takeTurn(x, y);
				}
			}
		} catch (IOException e) {
			isDisconnected = true;
			game.takeAiTurnIfDisconnected();
		} catch (IllegalMoveException e) {
			ServerGUI.logError("[" + name + "] recieved illegal move");
			sendKick("illegal move");
		}
		isDisconnected = true;
		game.takeAiTurnIfDisconnected();
		ServerGUI.log("[" + name + "] disconnected");
	}

	/**
	 * Sends a kick message to the player along with an reason.
	 */
	public void sendKick(String reason) {
		try {
			out.write(KICK + reason + "\n");
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Checks if the player is ready to start.
	 */
	//@ ensures \result == (getMinimumPlayers() != -1);
	/*@ pure */ public boolean isReady() {
		return minimumPlayers != -1;
	}

	/**
	 * Checks if the players is disconnected.
	 */
	/*@ pure */ public boolean isDisconnected() {
		return isDisconnected;
	}
}
