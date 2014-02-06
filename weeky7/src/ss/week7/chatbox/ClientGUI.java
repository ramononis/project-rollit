package ss.week7.chatbox;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * ClientGUI. A GUI for the client.
 * @author  Theo Ruys
 * @version 2005.02.21
 */
public class ClientGUI extends JFrame implements ActionListener, MessageUI {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4846675230307474920L;
	private JButton bConnect;
	private JButton bSend;
	private JTextField tfPort;
	private JTextArea taMessages;
	private Client client;
	private JTextField tfMessage;
	private JTextField tfName;
	private JTextField tfAddress;

	/** Constructs a ClientGUI object. */
	public ClientGUI() {
		super("ClientGUI");

		buildGUI();
		setVisible(true);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				e.getWindow().dispose();
			}
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	/** builds the GUI. */
	public void buildGUI() {
		setSize(600, 500);

		// Panel p1 - Listen

		JPanel p1 = new JPanel(new FlowLayout());
		JPanel pp = new JPanel(new GridLayout(6, 2));
		
		bSend = new JButton("Send message");
		bSend.addActionListener(this);
		
		JLabel lbAddress = new JLabel("Address: ");
		tfAddress = new JTextField(getHostAddress(), 12);

		JLabel lbPort = new JLabel("Port:");
		tfPort = new JTextField("2727", 5);

		JLabel lbName = new JLabel("Name: ");
		tfName = new JTextField();

		JLabel lbMessage = new JLabel("My message: ");
		tfMessage = new JTextField();

		JLabel emp1 = new JLabel("");
		JLabel emp2 = new JLabel("");
		


		pp.add(lbAddress);
		pp.add(tfAddress);
		pp.add(lbPort);
		pp.add(tfPort);
		pp.add(lbName);
		pp.add(tfName);
		pp.add(emp1);
		pp.add(emp2);
		pp.add(lbMessage);
		pp.add(tfMessage);
		pp.add(bSend);
		tfMessage.setEditable(false);


		bConnect = new JButton("Connect");
		bConnect.addActionListener(this);

		p1.add(pp, BorderLayout.WEST);
		p1.add(bConnect, BorderLayout.EAST);


		// Panel p2 - Messages

		JPanel p2 = new JPanel();
		p2.setLayout(new BorderLayout());

		JLabel lbMessages = new JLabel("Messages:");
		taMessages = new JTextArea("", 15, 50);
		taMessages.setEditable(false);
		p2.add(lbMessages);
		p2.add(taMessages, BorderLayout.SOUTH);

		Container cc = getContentPane();
		cc.setLayout(new FlowLayout());
		cc.add(p1);
		cc.add(p2);

	}

	/** returns the Internetadress of this computer */
	private String getHostAddress() {
		try {
			InetAddress iaddr = InetAddress.getLocalHost();
			return iaddr.getHostAddress();
		} catch (UnknownHostException e) {
			return "?unknown?";
		}
	}

	/**
	 * listener for the "Start Listening" and "Send Message" buttons
	 */
	public void actionPerformed(ActionEvent ev) {
		Object src = ev.getSource();
		if (src == bConnect) {
			startListening();
		}
		else if (src == bSend){
			client.sendMessage(tfMessage.getText());
			tfMessage.setText("");
		}
	}

	/**
	 * Construct a Client-object, which is waiting for clients. The port field and button should be disabled
	 */
	private void startListening() {
		int port = 0;
		//int max = 0;
		String clientName;
		InetAddress addr = null;

		try {
			addr = InetAddress.getByName(getHostAddress());
		} catch (UnknownHostException e) {
			addMessage("ERROR: not a valid host");
		}


		try {
			port = Integer.parseInt(tfPort.getText());
		} catch (NumberFormatException e) {
			addMessage("ERROR: not a valid portnumber!");
			return;
		}


		try {
			port = Integer.parseInt(tfPort.getText());
		} catch (NullPointerException e) {
			addMessage("ERROR: enter a name!");
			return;
		}

		clientName = tfName.getText();

		tfAddress.setEditable(false);
		tfName.setEditable(false);
		tfPort.setEditable(false);
		bConnect.setEnabled(false);
		tfMessage.setEditable(true);

		try {
			client = new Client(clientName, addr, port, this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		addMessage("Connecting to host " + getHostAddress() + " on port " + port + "...");
		
		client.start();
	}

	/** add a message to the textarea  */
	public void addMessage(String msg) {
		taMessages.append(msg + "\n");
	}

	/** Start a ClientGUI application */
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		ClientGUI gui = new ClientGUI();
	}

}
