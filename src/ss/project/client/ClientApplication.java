package ss.project.client;

import java.io.IOException;
import java.net.URL;

import ss.project.client.gui.ClientGUI;
import ss.project.server.logging.LoggingBootstrap;
public class ClientApplication {
	public static ClientGUI gui;
	public static boolean runningFromJar = false;
	
	public static void main(String[] args) throws IOException {
		final URL resource = ClientApplication.class.getResource("/resources/images/ICON.PNG");
		if (resource != null) {
			runningFromJar = true;
		}
		LoggingBootstrap.bootstrap();
		gui = new ClientGUI();
	}
}