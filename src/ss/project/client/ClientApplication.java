package ss.project.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;

import javax.swing.JOptionPane;

import ss.project.ai.DumbAi;
import ss.project.ai.NaiveAi;
import ss.project.ai.SmartAi;
import ss.project.ai.SuicideAi;
import ss.project.client.gui.ClientGUI;
import ss.project.engine.ComputerPlayer;
import ss.project.engine.HumanPlayer;
import ss.project.engine.Player;

public class ClientApplication {
	private static ClientGUI gui;
	private static Client client;
	public static boolean runningFromJar = false;

	public static void main(String[] args) throws IOException {

		final URL resource = ClientApplication.class
				.getResource("/resources/images/ICON.PNG");
		if (resource != null) {
			runningFromJar = true;
		}
		gui = new ClientGUI();
		client = null;
		int port = -1;
		InetAddress address;
		Player player = null;
		int ai = JOptionPane.showOptionDialog(gui, "Choose your player type",
				"Player type", JOptionPane.OK_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, new String[] { "Dumb",
						"Naive", "Suicide", "Smart", "Human" }, 2);
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
				player = new HumanPlayer("Ikke!");
				break;
		}
		while (client == null) {
			try {
				address = gui.askForIP();
				port = gui.askForPortNumber();
				client = new Client(address, port, player);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(gui,
						"Could not establish a connection.\nPlease try again.",
						"Could not establish a connection",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		// int minimumPlayers = JOptionPane.showOptionDialog(gui,
		// "Please select the minimal player amount.",
		// "Minimal player amount.", JOptionPane.OK_OPTION,
		// JOptionPane.QUESTION_MESSAGE, null, new Integer[] { 2, 3, 4 },
		// 2);
		// client.setMinimumPlayers(minimumPlayers);

		client.addObserver(gui);
		gui.setVisible(true);
	}

}
