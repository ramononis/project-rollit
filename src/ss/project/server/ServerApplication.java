package ss.project.server;

import java.util.Arrays;

import ss.project.server.gui.ServerGUI;
import ss.project.server.logging.LoggingBootstrap;

public class ServerApplication {
	public static void main(String[] args) {
		ServerGUI.setGUIEnabled(!(args.length > 0 && Arrays.asList(args)
				.contains("nogui")));
		LoggingBootstrap.bootstrap();
		Server server = new Server();
		server.setPort(ServerGUI.askForPortNumber());
		if (ServerGUI.isGUIEnabled()) {
			ServerGUI gui = new ServerGUI(server);
			server.addObserver(gui);
			gui.setVisible(true);
		}
	}
}