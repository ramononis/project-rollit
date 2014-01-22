package ss.project.client.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import ss.project.ai.DumbAi;
import ss.project.ai.SuicideAi;
import ss.project.engine.ComputerPlayer;
import ss.project.engine.Game;
import ss.project.engine.Player;

public class RolitView extends JFrame implements Observer {
	// CONSTANTS
	public static final Color RED = new Color(255, 0, 0);
	public static final Color GREEN = new Color(0, 255, 0);
	public static final Color BLUE = new Color(0, 0, 255);
	public static final Color YELLOW = new Color(255, 255, 0);
	public static final Color GREY = new Color(127, 127, 127);
	public static final Color WHITE = new Color(255, 255, 255);
	public static final int DIM = 50;
	public ArrayList<Player> players = new ArrayList<Player>();
	class RolitController implements ActionListener {
		private Game game;

		public RolitController(Game g) {
			game = g;
			for (int i = 0; i < game.getBoard().dim * game.getBoard().dim; i++) {
				boardButtons[i].addActionListener(this);
			}
			button.addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			for (int i = 0; i < game.getBoard().dim * game.getBoard().dim; i++) {
				if (boardButtons[i].equals(e.getSource())) {
					game.takeTurn(i);
				}
			}
			if (button.equals(e.getSource())) {
				game.reset(players);
				game.start();
			}
		}
	}

	private static final long serialVersionUID = -6378476084079904281L;

	public RolitView(Game g) {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		init(g);
		controller = new RolitController(g);
	}

	public JButton[] boardButtons;
	public JLabel label;
	public JButton button;
	public RolitController controller;

	private void init(Game g) {
		boardButtons = new JButton[g.getBoard().dim * g.getBoard().dim];
		label = new JLabel("Its RED's turn");
		button = new JButton("Another game?");
		Container c = getContentPane();
		Container board = new Container();
		GridLayout boardLayout = new GridLayout(g.getBoard().dim, g.getBoard().dim);
		board.setLayout(boardLayout);
		for (int i = 0; i < g.getBoard().dim * g.getBoard().dim; i++) {
			boardButtons[i] = new JButton();
			boardButtons[i].setPreferredSize(new Dimension(DIM, DIM));
			board.add(boardButtons[i]);
		}
		BoxLayout layout = new BoxLayout(c, BoxLayout.Y_AXIS);
		c.setLayout(layout);
		add(board);
		add(label);
		add(button);
		pack();
	}

	public static void main(String[] args) {
		Game game = new Game(8);
		RolitView view = new RolitView(game);
		game.addObserver(view);
		view.players.add(new ComputerPlayer(new DumbAi()));
		view.players.add(new ComputerPlayer(new SuicideAi()));
		game.reset(view.players);
		game.start();
		view.setVisible(true);
	}

	public void update(Observable o, Object arg) {
		Game game = (Game) o;
		for (int i = 0; i < game.getBoard().dim * game.getBoard().dim; i++) {
			JButton boardButton = boardButtons[i];
			boardButton.setEnabled(game.isValidMove(i)
					&& !game.getBoard().gameOver());
			//boardButton.setText(game.getBoard().getField(i).toString());
			switch(game.getBoard().getField(i)) {
				case BLUE:
					boardButton.setBackground(BLUE);
					break;
				case GREEN:
					boardButton.setBackground(GREEN);
					break;
				case RED:
					boardButton.setBackground(RED);
					break;
				case YELLOW:
					boardButton.setBackground(YELLOW);
					break;
				default:
					boardButton.setBackground(GREY);
					break;
			
			}
			if (game.isValidMove(i)) {
				boardButton.setBackground(WHITE);
			}
		}
		
		button.setEnabled(game.getBoard().gameOver());
		if (game.getBoard().isFull() && !game.getBoard().hasWinner()) {
			label.setText("Draw!");
		} else if (game.getBoard().hasWinner()) {
			label.setText("The winner is " + game.getPlayers().get(game.getWinner()).getName() + "(" + game.getWinner() + ")");
		} else {
			label.setText("It is " + game.getPlayers().get(game.getCurrent()).getName() + "(" + game.getCurrent() + ")\'s turn");
		}
	}
}
