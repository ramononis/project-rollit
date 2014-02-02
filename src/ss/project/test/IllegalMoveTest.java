package ss.project.test;

import java.net.InetAddress;

import javax.swing.JOptionPane;

import ss.project.ai.NaiveAi;
import ss.project.client.Client;
import ss.project.client.ClientGame;
import ss.project.model.Board;
import ss.project.model.ComputerPlayer;
import ss.project.model.Game;
import ss.project.model.Player;
import ss.project.server.Server;

public class IllegalMoveTest {
	public static void sleep() {
		try {
			Thread.sleep(400);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			Server server = new Server();
			sleep();
			server.setPort(0);
			int port = server.getPort();
			Player player1 = new Player("Player1") {
				@Override
				public void requestMove(Game g) {
					Board b = g.getBoard();
					int dim = b.getDimension();
					int move = -1;
					for (int i = 0; i < dim * dim && move == -1; i++) {
						if (!g.isValidMove(i)) {
							move = -1;
						}
					}
					((ClientGame) g).getClient().sendTurn(move);
				}
			};
			Client client1 = new Client(InetAddress.getLocalHost(), port,
					player1, 2) {
				@Override
				public void shutDown() {
					JOptionPane.showMessageDialog(null, "The test has succeeded", "Success!", JOptionPane.INFORMATION_MESSAGE);
					super.shutDown();
				}
			};
			client1.sendLogin();
			sleep();
			Player player2 = new ComputerPlayer(new NaiveAi());
			Client client2 = new Client(InetAddress.getLocalHost(), port,
					player2, 2);
			client2.sendLogin();
			sleep();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
