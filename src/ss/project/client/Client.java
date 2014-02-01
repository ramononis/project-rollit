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

import javax.swing.JOptionPane;

import ss.project.ProtocolConstants;
import ss.project.ai.DumbAi;
import ss.project.ai.NaiveAi;
import ss.project.ai.SmartAi;
import ss.project.ai.SuicideAi;
import ss.project.client.gui.ClientGUI;
import ss.project.engine.ComputerPlayer;
import ss.project.engine.HumanPlayer;
import ss.project.engine.Mark;
import ss.project.engine.Player;
import ss.project.exceptions.IllegalMoveException;

public class Client extends Observable implements Runnable, ProtocolConstants {
	private Socket socket;
	private BufferedReader in;
	private BufferedWriter out;
	private ClientGame game;
	private Mark myMark;
	private Player myPlayer;
	private String name;
	private int nrPlayers;
	private boolean welcomeRecieved;

	public static Client createClient(ClientGUI gui) {
		Client client = null;
		int port = -1;
		InetAddress address;
		String name;
		Player player = null;
		int nrPlayers = 2;
		int ai = JOptionPane.showOptionDialog(gui, "Choose your player type",
				"Player type", JOptionPane.OK_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, new String[] { "Dumb",
						"Naive", "Suicide", "Smart", "Human" }, 2);
		name = gui.askForName();
		switch (ai) {
		case 0:
			player = new ComputerPlayer(new DumbAi());
			break;
		case 1:
			player = new ComputerPlayer(new NaiveAi());
			break;
		case 2:
			player = new ComputerPlayer(new SuicideAi());
			break;
		case 3:
			player = new ComputerPlayer(new SmartAi());
			break;
		case 4:
			player = new HumanPlayer(name);
			break;
		}
		player.setName(name);
		while (client == null) {
			try {
				address = gui.askForIP();
				port = gui.askForPortNumber();
				nrPlayers = gui.askForPlayers();
				client = new Client(address, port, player, name, nrPlayers);
				client.sendLogin();
				System.out.println("CLIENTAPP");
			} catch (IOException e) {
				JOptionPane.showMessageDialog(gui,
						"Could not establish a connection.\nPlease try again.",
						"Could not establish a connection",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		return client;
	}

	public Mark getMyMark() {
		return myMark;
	}

	public void setMyMark(Mark mark) {
		myMark = mark;
	}

	public Client(InetAddress addr, int port, Player player, String n, int nr)
			throws IOException {
		nrPlayers = nr;
		name = n;
		socket = new Socket(addr, port);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(
				socket.getOutputStream()));
		setMyPlayer(player);
		new Thread(this).start();
		new Thread(new ConnectionChecker()).start();
	}

	@Override
	public void run() {
		try {
			while (true) {
				String line = in.readLine();
				System.out.println("[SERVER] " + line);
				if (line.contains(START_GAME)) {
					String playerNames = line.replaceAll(START_GAME, "");
					String[] playerNamesArray = playerNames.split(" ");

					int n = playerNamesArray.length;
					if (playerNamesArray[0].equals(name)) {
						setMyMark(Mark.RED);
					} else if (playerNamesArray[1].equals(name)) {
						setMyMark(Mark.GREEN);
					} else if (playerNamesArray[2].equals(name)) {
						setMyMark(Mark.BLUE);
					} else if (playerNamesArray[3].equals(name)) {
						setMyMark(Mark.YELLOW);
					}
					ArrayList<Player> players = new ArrayList<Player>();
					for (int i = 0; i < n; i++) {
						players.add(new Player(playerNamesArray[i]));
					}
					game = new ClientGame(this, players, 8);
					game.setMyPlayer(myPlayer);
					setChanged();
					notifyObservers(game);

				} else if (line.contains(WELCOME)) {
					welcomeRecieved = true;
					sendJoin();
				} else if (line.contains(KICK)) {
					JOptionPane.showMessageDialog(null,
							"You have been kicked from the server, Reason: " + line.replaceAll(KICK, "") + ".\n"
									+ "The client will shut down.",
							"Server disconnected", JOptionPane.ERROR_MESSAGE);
				} else if (line.contains(UPDATE_GUI)) {
					String line2 = line.replaceAll(UPDATE_GUI, "");
					String[] line2nstuff = line2.split(" ");
					int x = Integer.parseInt(line2nstuff[1]);
					int y = Integer.parseInt(line2nstuff[2]);
					game.takeTurn(x, y);

				}
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,
					"The server has disconnected.\n"
							+ "The client will shut down.",
					"Server disconnected", JOptionPane.ERROR_MESSAGE);
		} catch (IllegalMoveException e) {
			JOptionPane.showMessageDialog(null,
					"The server has parsed an illegal move.\n"
							+ "The client will shut down.",
					"Recieved illegal move", JOptionPane.ERROR_MESSAGE);

		}
		shutDown();
	}

	public void sendTurn(int i) {
		try {
			int dim = game.getBoard().dim;
			out.write(UPDATE_GUI + name + " " + (i / dim) + " " + (i % dim)
					+ "\n");
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendTurn(int r, int c) {
		sendTurn(r * game.getBoard().dim + c);
	}

	public void move(int i) {
	}

	public void shutDown() {
		try {
			socket.close();
		} catch (IOException e) {
		}
		System.exit(0);
	}

	public void sendJoin() {
		try {
			out.write(JOIN_GAME + nrPlayers + "\n");
			System.out.println(JOIN_GAME + nrPlayers + "\n");
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the myPlayer
	 */
	public Player getMyPlayer() {
		return myPlayer;
	}

	public void sendLogin() {
		try {
			out.write(LOGIN_GAME + name + "\n");
			System.out.println(LOGIN_GAME + name);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param myPlayer
	 *            the myPlayer to set
	 */
	public void setMyPlayer(Player player) {
		myPlayer = player;
	}

	public String getName() {
		return name;
	}
	
	class ConnectionChecker implements Runnable {

		public void run() {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(!welcomeRecieved) {
				JOptionPane.showMessageDialog(null,
						"The client hasn't recieved a welcome from server.\n"
								+ "The client will shut down.",
						"Server disconnected", JOptionPane.ERROR_MESSAGE);
				shutDown();
			}
		}
	}
}
