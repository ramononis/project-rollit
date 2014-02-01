package ss.project.server;

import ss.project.server.gui.ServerGUI;
import ss.project.server.logging.LoggingBootstrap;
public class ServerApplication {
	public static void main(String[] args) {
		LoggingBootstrap.bootstrap();
		Server server = new Server();
		ServerGUI gui = new ServerGUI(server);
		server.setPort(gui.askForPortNumber());
		server.addObserver(gui);
		gui.setVisible(true);
	}
}