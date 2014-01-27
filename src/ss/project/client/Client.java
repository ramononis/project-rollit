package ss.project.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;

import ss.project.engine.Game;
import ss.project.engine.Mark;
import ss.project.engine.Player;
import ss.project.gui.ProtocolConstants;

public class Client extends Observable implements Runnable, ProtocolConstants {
	private Socket socket;
	private BufferedReader in;
	private BufferedWriter out;
	private Game game;
	private Mark myMark;

	public Mark getMyMark() {
		return myMark;
	}

	public void setMyMark(Mark mark) {
		myMark = mark;
	}

	public Client(InetAddress addr, int port) throws IOException {
		socket = new Socket(addr, port);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(
				socket.getOutputStream()));
		new Thread(this).start();
	}

	@Override
	public void run() {
		try {
			while (true) {
				String line = in.readLine();
				System.out.print("bla");
				if (line.contains(START_GAME)) {
					int n = Integer.parseInt(""
							+ line.replaceAll(START_GAME, "").charAt(0));
					myMark = Mark.fromString(line.replaceAll(START_GAME, "")
							.substring(1));
					ArrayList<Player> players = new ArrayList<Player>();
					for (int i = 0; i < n; i++) {
						players.add(new Player("player " + (i + 1)));
					}
					game = new ClientGame(this, players, 8);
					setChanged();
					notifyObservers(game);
				} else if (line.contains(SEND_TURN)) {
					game.takeTurn(Integer.parseInt(line.replaceAll(SEND_TURN,
							"")));
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sendTurn(int i) {
		try {

			out.write(SEND_TURN + i + "\n");
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
