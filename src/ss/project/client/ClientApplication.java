package ss.project.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;

import javax.swing.JOptionPane;

import ss.project.client.gui.ClientGUI;

public class ClientApplication {
	public static ClientGUI gui;
	public static Client client;
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
		gui.setVisible(true);
	}
}