package ss.project.server;
import java.io.IOException;
import java.net.URL;

import ss.project.logging.LoggingBootstrap;
import ss.project.server.gui.GUI;
public class Application {
	public static GUI gui;
	public static boolean runningFromJar = false;
	public static void main(String[] args) throws IOException {
		final URL resource = Application.class.getResource("/resources/images/ICON.PNG");
		if (resource != null) {
			runningFromJar = true;
		}
		LoggingBootstrap.bootstrap();
		gui = new GUI();
	}
}