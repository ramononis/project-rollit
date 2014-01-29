package ss.project.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;

import ss.project.ProtocolConstants;
import ss.project.engine.Mark;
import ss.project.engine.Player;

public class Client extends Observable implements Runnable, ProtocolConstants {
	private Socket socket;
	private BufferedReader in;
	private BufferedWriter out;
	private ClientGame game;
	private Mark myMark;
	private int minimumPlayers = -1;
	private Player myPlayer;
	private String name;
	private int nrPlayers;

	public Mark getMyMark() {
		return myMark;
	}

	public void setMyMark(Mark mark) {
		myMark = mark;
	}

	public Client(InetAddress addr, int port, Player player, String n, int nr) throws IOException {
		nrPlayers = nr;
		name = n;
		socket = new Socket(addr, port);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(
				socket.getOutputStream()));
		setMyPlayer(player);
		new Thread(this).start();
	}

	@Override
	public void run() {
		try {
			while (true) {
				String line = in.readLine();
				System.out.println("ONTVANGEN!!!: " + line);
				if (line.contains(START_GAME)) {
					String playerNames = line.replaceAll(START_GAME, "");
					String[] playerNamesArray = playerNames.split(" ");
					
					int n = playerNamesArray.length;
					if(playerNamesArray[0].equals(name)) {
						setMyMark(Mark.RED);
					} else if(playerNamesArray[1].equals(name)) {
						setMyMark(Mark.GREEN);
					} else if(playerNamesArray[2].equals(name)) {
						setMyMark(Mark.BLUE);
					} else if(playerNamesArray[3].equals(name)) {
						setMyMark(Mark.YELLOW);
					}
					ArrayList<Player> players = new ArrayList<Player>();
					for (int i = 0; i < n; i++) {
						players.add(new Player(playerNamesArray[i]));
					}
					game = new ClientGame(this, players, 8);
					game.setMyPlayer(myPlayer);
					setChanged();
					notifyObservers(game);
					
				} 
				
				else if (line.contains(WELCOME)){
					sendJoin();
				}
				
				else if (line.contains(WELCOME)){
					
				}
				else if (line.contains(UPDATE_GUI)){
					String line2 = line.replaceAll(UPDATE_GUI, "");
					String[] line2nstuff = line2.split(" ");
					int x  = Integer.parseInt(line2nstuff[1]);
					int y  = Integer.parseInt(line2nstuff[2]);
					game.takeTurn(x, y);
				}
				
				else if (line.contains(SEND_TURN)) {
					game.takeTurn(Integer.parseInt(line.replaceAll(SEND_TURN,
							"")));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendTurn(int i) {
		try {
			int dim = game.getBoard().dim;
			int x = i / dim;
			int y = i % dim;
			out.write(SEND_TURN + x + " " + y + "\n");
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendTurn(int r, int c){
		sendTurn(r * game.getBoard().dim + c);
	}
	
	public void move(int i){
	}

	/**
	 * @return the minimumPlayers
	 */
	public int getMinimumPlayers() {
		return minimumPlayers;
	}
	
	public void sendJoin(){
		try {
			out.write(JOIN_GAME + nrPlayers + "\n");
			System.out.println(JOIN_GAME + nrPlayers + "\n");
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param minimumPlayers
	 *            the minimumPlayers to set
	 */
	public void setMinimumPlayers(int minimum) {
		minimumPlayers = minimum;
		try {
			//out.write(MINIMAL_PLAYERS + minimum + "\n");
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the myPlayer
	 */
	public Player getMyPlayer() {
		return myPlayer;
	}
	
	public void sendLogin(){
		try {
			out.write(LOGIN_GAME + name + "\n");
			System.out.println(LOGIN_GAME + name);
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param myPlayer the myPlayer to set
	 */
	public void setMyPlayer(Player myPlayer) {
		this.myPlayer = myPlayer;
	}
	
	public String getName(){
		return this.name;
	}
}
