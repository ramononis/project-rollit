package ss.project.client.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.Box;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.FontUIResource;

import ss.project.client.ClientApplication;
import ss.project.server.Server;

public class ClientGUI extends JFrame implements ActionListener {
	private static final long serialVersionUID = -4411033752001988794L;

	static {
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
	}
	private JToolBar toolBar;

	public ClientGUI() {
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
		setTitle("Rollit Client");
		setResizable(true);
		setMinimumSize(new Dimension(800, 600));
		setLocationRelativeTo(getOwner());
		setExtendedState(this.getExtendedState() | ClientGUI.MAXIMIZED_BOTH);
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
		}
	}

	private JMenuBar constructMenu() {
		final String[] titles = new String[] { "Settings", "Info", "Je moeder" };
		final String[][] elements = new String[][] { {}, { "Info", "Authors" },
				{ "dus" } };
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
						jmi.addActionListener(this);
						jmi.setActionCommand(title + "." + e.replace("$", ""));
						menu.add(jmi);
					} else {
						JMenuItem jmi;
						jmi = new JMenuItem(e);
						jmi.addActionListener(this);
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
		if (ClientApplication.runningFromJar) {
			setIconImage(Toolkit.getDefaultToolkit().getImage(
					getClass().getResource("/resources/images/ICON.PNG")));
		}
		addWindowListener(new WindowAdapter() {
			public void windowClosing(final WindowEvent e) {
				int close = JOptionPane.showConfirmDialog(ClientGUI.this,
						"Close the application?", "Close?",
						JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if (close == JOptionPane.OK_OPTION) {
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
		add(toolBar, BorderLayout.NORTH);
	}

	public InetAddress askForIP() {
		InetAddress address = null;
		while (address == null) {
			String inputString = JOptionPane.showInputDialog(this,
					"Enter the server's IP address", "Enter IP address",
					JOptionPane.QUESTION_MESSAGE);
			try {
				address = InetAddress.getByName(inputString);
			} catch (UnknownHostException e) {
				JOptionPane.showMessageDialog(this,
						"Please enter a valid IP address",
						"IP address invalid", JOptionPane.ERROR_MESSAGE);
			}
		}
		return address;
	}

	public int askForPortNumber() {
		int port = -1;
		while (port == -1) {
			String inputString = JOptionPane.showInputDialog(this,
					"Enter a port number between 1100 and 65535: ",
					"Port number", JOptionPane.QUESTION_MESSAGE);
			try {
				port = Integer.parseInt(inputString);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(this,
						"Please enter a valid number", "Number Invalid",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		return port;
	}
}