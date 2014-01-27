package ss.project.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ServerPeer {
	public static final String EXIT = "exit";

	protected String name;
	protected Socket sock;
	protected BufferedReader in;
	protected BufferedWriter out;
	private int minimumPlayers = 2;

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
		this.name = nameArg;
		this.sock = sockArg;
		this.in = new BufferedReader(new InputStreamReader(
				sockArg.getInputStream()));
		this.out = new BufferedWriter(new OutputStreamWriter(
				sockArg.getOutputStream()));
	}

	/**
	 * Closes the connection, the sockets will be terminated
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

	/** returns name of the peer object */
	public String getName() {
		return name;
	}

	/** read a line from the default input */
	static public String readString(String tekst) {
		System.out.print(tekst);
		String antw = null;
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					System.in));
			antw = in.readLine();
		} catch (IOException e) {
		}

		return (antw == null) ? "" : antw;
	}

	public int getMinimumPlayers() {
		return minimumPlayers;
	}

	public void setMinimumPlayers(int minimum) {
		minimumPlayers = minimum;
	}
}
