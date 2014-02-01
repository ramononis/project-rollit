package ss.project.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import ss.project.ProtocolConstants;
import ss.project.engine.Mark;
import ss.project.engine.Player;
import ss.project.exceptions.IllegalMoveException;
import ss.project.server.gui.ServerGUI;

public class ServerPeer implements Runnable, ProtocolConstants {
	public static final String EXIT = "exit";
	protected String name;
	protected Socket sock;
	protected BufferedReader in;
	protected BufferedWriter out;
	private int minimumPlayers = -1;
	private ServerGame game;
	private Server server;
	private boolean isDisconnected = false;

	/*
	 * @ requires (nameArg != null) && (sockArg != null);
	 */
	/**
	 * Constructor. creates a peer object based in the given parameters.
	 * 
	 * @param nameArg
	 *            name of the Peer-proces
	 * @param sockArg
	 *            Socket of the Peer-proces
	 */
	public ServerPeer(Server svr, String nameArg, Socket sockArg)
			throws IOException {
		name = nameArg;
		sock = sockArg;
		server = svr;
		in = new BufferedReader(new InputStreamReader(sockArg.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(
				sockArg.getOutputStream()));
		new Thread(this).start();
	}

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
		if (!isDisconnected) {
			try {
				in.close();
				out.close();
				sock.close();
				System.out.println("Exited.");

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * returns name of the peer object.
	 */
	public String getName() {
		return name;
	}

	public int getMinimumPlayers() {
		return minimumPlayers;
	}

	public void setMinimumPlayers(int minimum) {
		minimumPlayers = minimum;
	}

	public void sendName(String n) {
		if (!isDisconnected) {
			try {
				out.write(JOIN_GAME + n);
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

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

	public void sendTurn(int i) {
		if (!isDisconnected) {
			int dim = game.getBoard().dim;
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
	 * @return the game
	 */
	public ServerGame getGame() {
		return game;
	}

	/**
	 * @param game
	 *            the game to set
	 */
	public void setGame(ServerGame g) {
		game = g;
	}

	@Override
	public void run() {
		try {
			while (true) {
				String line = in.readLine();
				ServerGUI.log("[" + name + "] " + line);
				if (line.contains(LOGIN_GAME)) {
					name = line.replaceAll(LOGIN_GAME, "");
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
			kick("illegal move");
		}
		isDisconnected = true;
		game.takeAiTurnIfDisconnected();
		ServerGUI.log("[" + name + "] disconnected");
	}

	public void kick(String message) {
		try {
			out.write(KICK + message + "\n");
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isReady() {
		return minimumPlayers != -1;
	}

	public boolean isDisconnected() {
		return isDisconnected;
	}
}
