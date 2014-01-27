package ss.project.server.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.FontUIResource;

import ss.project.server.Server;
import ss.project.server.ServerApplication;
import ss.project.server.logging.TextAreaLogHandler;

public class ServerGUI extends JFrame implements Observer {
	private static final long serialVersionUID = -4411033752001988794L;
	private ServerController controller;

	class ServerController implements ActionListener {
		private Server server;

		public ServerController(Server s) {
			server = s;

		}

		public void actionPerformed(final ActionEvent e) {
			String action = e.getActionCommand();
			final int z = action.indexOf('.');
			final String[] command = new String[2];
			if (z == -1) {
				command[0] = action;
				command[1] = "";
			} else {
				command[0] = action.substring(0, z);
				command[1] = action.substring(z + 1);
			}
			if (command[0].equals("Info")) {
				if (command[1].equals("Info")) {
					JOptionPane.showMessageDialog(ServerGUI.this, new String[] {
							"University of Twente-Bachelor Computer Science",
							"Software Systems Module Programming Project" },
							"Info", JOptionPane.INFORMATION_MESSAGE);
				}
				if (command[1].equals("Authors")) {
					JOptionPane.showMessageDialog(ServerGUI.this,
							new String[] { "Group 14 - Ramon Onis, Tim Blok" },
							"Info", JOptionPane.INFORMATION_MESSAGE);
				}
			}
			if (command[0].equals("Settings")) {
				if (command[1].equals("Hide log")) {
					textScroll.setVisible(!((JCheckBoxMenuItem) e.getSource())
							.isSelected());
					if ((getExtendedState() & Frame.MAXIMIZED_BOTH) != Frame.MAXIMIZED_BOTH) {
						Dimension size = getSize();
						log("size");
						pack();
						size.height += textScroll.getSize().height
								* (((JCheckBoxMenuItem) e.getSource())
										.isSelected() ? -1 : 1);
						setSize(size);
					} else {
						pack();
						setExtendedState(getExtendedState()
								| JFrame.MAXIMIZED_BOTH);
					}
				}
			}
		}
	}

	static {
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
	}
	static Logger log;
	private JToolBar toolBar;
	private JScrollPane textScroll;

	public ServerGUI(Server s) {
		controller = new ServerController(s);
		UIManager.put("ToolTip.font", new FontUIResource("SansSerif",
				Font.BOLD, 22));
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		initializeGUI();
		setTitle("Rollit Server");
		setResizable(true);
		setMinimumSize(new Dimension(800, 600));
		setLocationRelativeTo(getOwner());
		setExtendedState(this.getExtendedState() | ServerGUI.MAXIMIZED_BOTH);
		log = Logger.getLogger(ServerGUI.class.getName());
		log("STARTUP SUCCESFULL");
	}

	public int askForPortNumber() {
		int port = -1;
		while (port == -1) {
			String inputString = JOptionPane.showInputDialog(this,
					"Enter a port number between 1100 and 65535: ",
					"Port number", JOptionPane.QUESTION_MESSAGE);
			try {
				int inputInt = Integer.parseInt(inputString);
				if (Server.portAvailable(inputInt)) {
					port = inputInt;
				} else {
					JOptionPane
							.showMessageDialog(
									this,
									"This port is invalid or not available.\nPlease enter an other port number.",
									"Port invalid", JOptionPane.ERROR_MESSAGE);
				}
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(this,
						"Please enter a valid number", "Number Invalid",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		return port;
	}

	public static void log(String message) {
		ServerGUI.log.info(message);
	}

	public static void logError(String message) {
		ServerGUI.log.log(Level.WARNING, "ERROR: " + message);
	}

	public void actionPerformed(final ActionEvent e) {
		String action = e.getActionCommand();
		final int z = action.indexOf('.');
		final String[] command = new String[2];
		if (z == -1) {
			command[0] = action;
			command[1] = "";
		} else {
			command[0] = action.substring(0, z);
			command[1] = action.substring(z + 1);
		}
		if (command[0].equals("Info")) {
			if (command[1].equals("Info")) {
				JOptionPane.showMessageDialog(this, new String[] {
						"University of Twente-Bachelor Computer Science",
						"Software Systems Module Programming Project" },
						"Info", JOptionPane.INFORMATION_MESSAGE);
			}
			if (command[1].equals("Authors")) {
				JOptionPane.showMessageDialog(this,
						new String[] { "Group 14 - Ramon Onis, Tim Blok" },
						"Info", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		if (command[0].equals("Settings")) {
			if (command[1].equals("Hide log")) {
				textScroll.setVisible(!((JCheckBoxMenuItem) e.getSource())
						.isSelected());
				if ((getExtendedState() & Frame.MAXIMIZED_BOTH) != Frame.MAXIMIZED_BOTH) {
					Dimension size = getSize();
					log("size");
					pack();
					size.height += textScroll.getSize().height
							* (((JCheckBoxMenuItem) e.getSource()).isSelected() ? -1
									: 1);
					setSize(size);
				} else {
					pack();
					setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
				}
			}
		}
	}

	private JMenuBar constructMenu() {
		final String[] titles = new String[] { "Settings", "Info" };
		final String[][] elements = new String[][] { { "$Hide log" },
				{ "Info", "Authors" } };
		final JMenuBar bar = new JMenuBar();
		for (int i = 0; i < titles.length; i++) {
			final String title = titles[i];
			final JMenu menu = new JMenu(title);
			final String[] elems = elements[i];
			for (String e : elems) {
				if (e.equals("-")) {
					menu.add(new JSeparator());
				} else {
					if (e.startsWith("$")) {
						JCheckBoxMenuItem jmi = new JCheckBoxMenuItem(
								e.replace("$", ""));
						jmi.addActionListener(controller);
						jmi.setActionCommand(title + "." + e.replace("$", ""));
						menu.add(jmi);
					} else {
						JMenuItem jmi;
						jmi = new JMenuItem(e);
						jmi.addActionListener(controller);
						jmi.setActionCommand(title + "." + e);
						menu.add(jmi);
					}
				}

			}
			bar.add(menu);
		}
		return bar;
	}

	private void initializeGUI() {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		if (ServerApplication.runningFromJar) {
			setIconImage(Toolkit.getDefaultToolkit().getImage(
					getClass().getResource("/resources/images/ICON.PNG")));
		}
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
		final JMenuBar bar = constructMenu();
		setJMenuBar(bar);
		toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.add(Box.createHorizontalGlue());
		textScroll = new JScrollPane(TextAreaLogHandler.TEXT_AREA,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		textScroll.setBorder(null);
		textScroll.setVisible(true);
		add(toolBar, BorderLayout.NORTH);
		add(textScroll, BorderLayout.SOUTH);
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub

	}
}