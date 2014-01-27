package ss.project.server;
import java.io.IOException;
import java.net.URL;

import ss.project.server.gui.ServerGUI;
import ss.project.server.logging.LoggingBootstrap;
public class ServerApplication {
	public static ServerGUI gui;
	public static Server server;
	public static boolean runningFromJar = false;
	public static void main(String[] args) throws IOException {
		final URL resource = ServerApplication.class.getResource("/resources/images/ICON.PNG");
		if (resource != null) {
			runningFromJar = true;
		}
		LoggingBootstrap.bootstrap();
		server = new Server();
		gui = new ServerGUI(server);
		server.setPort(gui.askForPortNumber());
		gui.setVisible(true);
	}
}