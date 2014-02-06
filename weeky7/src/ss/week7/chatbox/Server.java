package ss.week7.chatbox;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashSet;

/**
 * P2 prac wk5. <br>
 * Server. A Thread class that listens to a socket connection on a 
 * specified port. For every socket connection with a Client, a new 
 * ClientHandler thread is started. 
 * @author  Theo Ruys
 * @version 2005.02.21
 */
public class Server extends Thread {
	private int port;
	private MessageUI mui;
	private Collection<ClientHandler> threads = new HashSet<ClientHandler>();

	/** Constructs a new Server object */
	public Server(int portArg, MessageUI muiArg) {
		this.port = portArg;
		this.mui = muiArg;
	}

	/**
	 * Listens to a port of this Server if there are any Clients that 
	 * would like to connect. For every new socket connection a new
	 * ClientHandler thread is started that takes care of the further
	 * communication with the Client. 
	 */
	public void run() {
		ServerSocket ssock = null;

		// create server socket

		try {
			ssock = new ServerSocket(port);
			mui.addMessage("server" + " successfully created on port " + port + ".");
		} catch (IOException e){
			mui.addMessage("Het ging echt gigantisch stuk :(((");
		}


		try {
			while(true){
				System.out.println("begin accepten");
				Socket s = ssock.accept();
				System.out.println("iemand geaccept");

				final ClientHandler handler = new ClientHandler(this ,s);
				mui.addMessage("starting handler.");
				addHandler(handler);
				handler.start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			mui.addMessage("Client disconencted or something.");
		}
		mui.addMessage("Closing");


	}

	/**
	 * Sends a message using the collection of connected ClientHandlers
	 * to all connected Clients.
	 * @param msg message that is send
	 */
	public void broadcast(String msg) {
		if(threads.isEmpty()){
			mui.addMessage("Geen clients meer, je bent alleen nu.");
		}

		else{
			for(ClientHandler ch : threads){
				ch.sendMessage(msg);
			}
		}
		mui.addMessage(msg);
	}

	/**
	 * Add a ClientHandler to the collection of ClientHandlers.
	 * @param handler ClientHandler that will be added
	 */
	public void addHandler(ClientHandler handler) {
		threads.add(handler);
	}

	/**
	 * Remove a ClientHandler from the collection of ClientHanlders. 
	 * @param handler ClientHandler that will be removed
	 */
	public void removeHandler(ClientHandler handler) {
		threads.remove(handler);
	}

} // end of class Server
