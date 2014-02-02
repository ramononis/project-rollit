package ss.project.server.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import ss.project.model.Mark;
import ss.project.gui.RolitView;
import ss.project.gui.ScorePanel;
import ss.project.server.Server;
import ss.project.server.ServerGame;
import ss.project.server.logging.TextAreaLogHandler;

public class ServerGUI extends JFrame implements Observer {
	private static final long serialVersionUID = -4411033752001988794L;
	private JTabbedPane tabbedPane;
	private static Logger log;
	private int gameAmount = 0;
	private static boolean enableGUI;
	private JScrollPane textScroll;

	public ServerGUI(Server s) {
		initializeGUI();
		setTitle("Rollit Server");
		setResizable(true);
		setSize(new Dimension(500, 600));
		log = Logger.getLogger(ServerGUI.class.getName());
		log("STARTUP SUCCESFULL");
	}

	public static int askForPortNumber() {
		int port = -1;
		while (port == -1) {
			String inputString = JOptionPane.showInputDialog(null,
					"Enter a port number between 1100 and 65535: ",
					"Port number", JOptionPane.QUESTION_MESSAGE);
			if (inputString == null) {
				// cancel button
				System.exit(0);
			}
			try {
				int inputInt = Integer.parseInt(inputString);
				if (Server.portAvailable(inputInt)) {
					port = inputInt;
				} else {
					JOptionPane.showMessageDialog(null,
							"This port is invalid or not available.\n"
									+ "Please enter an other port number.",
							"Port invalid", JOptionPane.ERROR_MESSAGE);
				}
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null,
						"Please enter a valid number", "Number Invalid",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		return port;
	}

	public static void log(String message) {
		if (enableGUI) {
			ServerGUI.log.info(message);
		} else {
			System.out.println(message);
		}
	}

	public static void logError(String message) {
		if (enableGUI) {
			ServerGUI.log.log(Level.WARNING, "ERROR: " + message);
		} else {
			System.err.println(message);
		}
	}

	private void initializeGUI() {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(final WindowEvent e) {
				int close = JOptionPane.showConfirmDialog(ServerGUI.this,
						"Close the application?", "Close?",
						JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if (close == JOptionPane.OK_OPTION) {
					log("EXITING");
					System.exit(0);
				}
				return;
			}
		});
		textScroll = new JScrollPane(TextAreaLogHandler.TEXT_AREA,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		textScroll.setBorder(null);
		textScroll.setVisible(true);
		tabbedPane = new JTabbedPane();
		add(tabbedPane, BorderLayout.CENTER);
		add(textScroll, BorderLayout.SOUTH);
	}

	@Override
	public void update(Observable o, Object arg) {

		if (o instanceof Server) {
			if (arg instanceof ServerGame) {
				ServerGame game = (ServerGame) arg;
				RolitView gameView = new RolitView(game, Mark.EMPTY);
				game.addObserver(gameView);
				ScorePanel scorePanel = new ScorePanel(game);
				game.addObserver(scorePanel);
				gameView.add(scorePanel);
				tabbedPane.add("Game " + (++gameAmount), gameView);
			}
		}
	}

	/**
	 *@return the enableGUI
	 */
	public static boolean isGUIEnabled() {
		return enableGUI;
	}

	/**
	 *@param enableGUI
	 *            the enableGUI to set
	 */
	public static void setGUIEnabled(boolean enableGUI) {
		ServerGUI.enableGUI = enableGUI;
	}
}