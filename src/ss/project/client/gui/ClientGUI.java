package ss.project.client.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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

import ss.project.client.ClientApplication;
import ss.project.server.logging.TextAreaLogHandler;

public class ClientGUI extends JFrame implements ActionListener {
	private static final long serialVersionUID = -4411033752001988794L;

	static {
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
	}
	static Logger log;
	private JToolBar toolBar;
	private JScrollPane textScroll;

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
		setVisible(true);
		log = Logger.getLogger(ClientGUI.class.getName());
		log("STARTUP SUCCESFULL");
	}

	public void log(String message) {
		ClientGUI.log.info(message);
	}

	public void logError(String message) {
		ClientGUI.log.log(Level.WARNING, "ERROR: " + message);
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
						new String[] {"Group 14 - Ramon Onis, Tim Blok"},
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
		final String[] titles = new String[] {"Settings", "Info", "Je moeder"};
		final String[][] elements = new String[][] {{"$Hide log"}, {"Info", "Authors"}, {"dus"}};
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
}