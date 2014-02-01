package ss.project.client;

import ss.project.client.gui.ClientGUI;

public class ClientApplication {
	public static void main(String[] args) {
		ClientGUI gui = new ClientGUI();
		Client client = Client.createClient(gui);
		client.addObserver(gui);
		gui.setVisible(true);
	}

}
