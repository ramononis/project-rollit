
package ss.week7.cmdline;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server. 
 * @author  Theo Ruys
 * @version 2005.02.21
 */
public class Server {
	private static final String USAGE = "usage: java week4.cmdline.Server <name> <port>";

	/** Start een Server-applicatie op. */
	public static void main(String[] args) {
			if (args.length >= 3) {
				System.out.println(USAGE);
				System.exit(0);
			}

			String name = args[0];
			int port = 0;
			ServerSocket ssock = null;


			// check args[1] - the port
			try {
				port = Integer.parseInt(args[1]);
			} catch ( NumberFormatException e) {
				System.out.println ( USAGE );
				System.out.println(" ERROR : port " + args[2] + " is not an integer " );
				System.exit(0);
			}
			
			// create server socket
			
			try {
				ssock = new ServerSocket(port);
				System.out.println(name + " successfully created on port " + port + ".");
			} catch (IOException e){
				System.out.println("Het ging echt gigantisch stuk :(((");
			}
			

			int i = 0;
			
			
			try {
				while(true){
					Socket s = ssock.accept();
					i++;
					System.out.println("[Client no. " + i + " has connected]");
					final Peer peer = new Peer("peer" + i,s);
					
					new Thread(peer).start();
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							peer.handleTerminalInput();
						}
					}).start();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

	} // end of class Server
