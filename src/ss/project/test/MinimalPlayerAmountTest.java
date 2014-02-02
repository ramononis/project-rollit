package ss.project.test;

import java.net.InetAddress;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import javax.swing.JOptionPane;

import ss.project.client.Client;
import ss.project.model.Game;
import ss.project.model.Player;
import ss.project.server.Server;

public class MinimalPlayerAmountTest implements Observer {
	private int clientsCount = 50;

	public static void sleep() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public MinimalPlayerAmountTest() {

	}

	public void runTest() {
		try {
			Server server = new Server();
			sleep();
			server.setPort(0);
			int port = server.getPort();
			for (int i = 0; i < 50; i++) {
				Player player = new Player("" + i);
				Client client = new Client(InetAddress.getLocalHost(), port,
						player, 2 + new Random().nextInt(3));
				client.addObserver(this);
				client.sendLogin();
				sleep();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new MinimalPlayerAmountTest().runTest();
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof Client) {
			Client client = (Client) o;
			if (arg instanceof Game) {
				Game game = (Game) arg;
				if (game.getPlayers().size() < client.getMinimalPlayers()) {
					JOptionPane.showMessageDialog(null, "The test has failed",
							"FAIL!", JOptionPane.INFORMATION_MESSAGE);
					System.exit(0);
				} else {
					clientsCount--;
				}
				if (clientsCount == 3) {
					JOptionPane.showMessageDialog(null,
							"The test has succeeded", "Success!!",
							JOptionPane.INFORMATION_MESSAGE);
					System.exit(0);
				}
			}
		}
	}
}
