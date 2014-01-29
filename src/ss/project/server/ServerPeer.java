package ss.project.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import ss.project.ProtocolConstants;
import ss.project.engine.Mark;

public class ServerPeer implements Runnable, ProtocolConstants {
	public static final String EXIT = "exit";
	protected String name;
	protected Socket sock;
	protected BufferedReader in;
	protected BufferedWriter out;
	private int minimumPlayers = -1;
	private ServerGame game;

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
	public ServerPeer(String nameArg, Socket sockArg) throws IOException {
		name = nameArg;
		sock = sockArg;
		in = new BufferedReader(new InputStreamReader(sockArg.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(
				sockArg.getOutputStream()));
		new Thread(this).start();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	public void sendStart(int n, Mark mark) {

		try {
			out.write(START_GAME + n + mark.toString() + "\n");
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sendTurn(int i) {
		try {
			System.out.println(this);
			out.write(SEND_TURN + i + "\n");
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
				System.out.print(line);
				if (line.contains(SEND_TURN)) {
					game.takeTurn(Integer.parseInt(line.replaceAll(SEND_TURN,
							"")));
				}// else //if (line.contains(MINIMAL_PLAYERS)) {
					//minimumPlayers = Integer.parseInt(line.replaceAll(
				//			MINIMAL_PLAYERS, ""));
				//}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isReady() {
		return true; //minimumPlayers != -1;
	}
}
