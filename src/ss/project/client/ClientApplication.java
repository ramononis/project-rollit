package ss.project.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;

import javax.swing.JOptionPane;
import ss.project.client.gui.ClientGUI;

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
		while (client == null) {
			try {
				address = gui.askForIP();
				port = gui.askForPortNumber();
				client = new Client(address, port);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(gui,
						"Could not establish a connection.\nPlease try again.",
						"Could not establish a connection",
						JOptionPane.ERROR_MESSAGE);
			}
		}
//		int minimumPlayers = JOptionPane.showOptionDialog(gui,
//				"Please select the minimal player amount.",
//				"Minimal player amount.", JOptionPane.OK_OPTION,
//				JOptionPane.QUESTION_MESSAGE, null, new Integer[] { 2, 3, 4 },
//				2);
//		client.setMinimumPlayers(minimumPlayers);
		client.addObserver(gui);
		gui.setVisible(true);
	}

}
