package ss.project.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
	private Socket socket;
	public Client(InetAddress addr, int port) throws IOException {
		socket = new Socket(addr, port);
	}
}
