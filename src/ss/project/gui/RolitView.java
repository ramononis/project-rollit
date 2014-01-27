package ss.project.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import ss.project.engine.Game;
import ss.project.engine.HumanPlayer;
import ss.project.engine.Mark;
import ss.project.engine.Player;

public class RolitView extends Container implements Observer {
	// CONSTANTS
	public static final Color WHITE = new Color(255, 255, 255);

	private Mark usersMark = null;
	public static final int DIM = 50;
	private ArrayList<Player> players = new ArrayList<Player>();
	private static ScorePanel scorePanel;

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
			System.out.print("bla");
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
		init(g);
		controller = new RolitController(g);
	}

	public RolitView(Game g, Mark mark) {
		this(g);
		setUsersMark(mark);
	}

	public JButton[] boardButtons;
	public JLabel label;
	public JButton button;
	public RolitController controller;

	private void init(Game g) {
		boardButtons = new JButton[g.getBoard().dim * g.getBoard().dim];
		label = new JLabel("Its RED's turn");
		button = new JButton("Another game?");
		Container board = new Container();
		GridLayout boardLayout = new GridLayout(g.getBoard().dim,
				g.getBoard().dim);
		board.setLayout(boardLayout);
		for (int i = 0; i < g.getBoard().dim * g.getBoard().dim; i++) {
			boardButtons[i] = new JButton();
			boardButtons[i].setPreferredSize(new Dimension(DIM, DIM));
			board.add(boardButtons[i]);
		}
		BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(layout);
		add(board);
		add(label);
		add(button);
	}

	public static void main(String[] args) {
		Game game = new Game(8);
		RolitView view = new RolitView(game);
		game.addObserver(view);
		view.players.add(new HumanPlayer("Ramon"));
		view.players.add(new HumanPlayer("Ramon"));
		view.players.add(new HumanPlayer("Ramon"));
		view.players.add(new HumanPlayer("Ramon"));
		game.reset(view.players);
		scorePanel = new ScorePanel(game);
		game.addObserver(scorePanel);
		view.add(scorePanel);
		game.start();
		JFrame frame = new JFrame();
		frame.add(view);
		frame.setVisible(true);
	}

	public void update(Observable o, Object arg) {
		Game game = (Game) o;
		for (int i = 0; i < game.getBoard().dim * game.getBoard().dim; i++) {
			JButton boardButton = boardButtons[i];
			boardButton.setEnabled(game.getCurrent().equals(usersMark)
					&& game.isValidMove(i) && !game.getBoard().gameOver());
			boardButton.setBackground(game.getBoard().getField(i).toColor());
			if (game.isValidMove(i)) {
				boardButton.setBackground(WHITE);
			}
		}

		button.setEnabled(game.getBoard().gameOver());
		if (game.getBoard().isFull() && !game.getBoard().hasWinner()) {
			label.setText("Draw!");
		} else if (game.getBoard().hasWinner()) {
			label.setText("The winner is "
					+ game.getPlayers().get(game.getWinner()).getName() + "("
					+ game.getWinner() + ")");
		} else {
			label.setText("It is "
					+ game.getPlayers().get(game.getCurrent()).getName() + "("
					+ game.getCurrent() + ")\'s turn");
		}
	}

	public Mark getUsersMark() {
		return usersMark;
	}

	public void setUsersMark(Mark mark) {
		if (mark != null) {
			usersMark = mark;
		}
	}
}
