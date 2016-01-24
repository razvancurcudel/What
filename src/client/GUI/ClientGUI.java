package client.GUI;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;

import client.Client;
import data.Packet;

/**
 * @author Curcudel Ioan-Razvan
 */

public class ClientGUI implements Runnable {

	private static final int	WIDTH		= 540;
	private static final int	HEIGHT		= 400;

	private Client				client		= null;

	public static String		toSend		= "";
	String						incoming	= "";

	private JFrame				frame		= null;
	private CardLayout			cl			= null;

	private JPanel				content		= null;	// main panel
	private JPanel				dataPanel	= null;	// first panel
	private JPanel				chatPanel	= null;	// chat panel

	// #################### DATA PANEL ####################
	private JTabbedPane			tabs		= null;

	// ~~~~~~~~~~~~~~~~~~~~~ LogInTab ~~~~~~~~~~~~~~~~~~~~~
	private JPanel				logInTab	= null;
	private JTextField			username;
	private JTextField			password;
	private JButton				logInButton;

	// ~~~~~~~~~~~~~~~~~~~~~ SignUpTab ~~~~~~~~~~~~~~~~~~~~~
	private JPanel				signUpTab	= null;

	// private JPanel changePassTab = null; //TODO change pass

	// #################### CHAT PANEL ####################
	private JTextArea			textArea;
	private JTextField			input;

	public ClientGUI(Client client) {
		this.client = client;
	}

	private void initFrame() {
		frame = new JFrame();
		frame.setBounds(0, 0, WIDTH, HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
	}

	private void initPanels() {
		cl = new CardLayout();
		content = new JPanel();
		content.setLayout(cl);

		logInTab = createLogInPanel();
		signUpTab = new JPanel();
		chatPanel = createChatPanel();

		tabs = new JTabbedPane();
		tabs.add("LogIn", logInTab);
		tabs.add("SignUp", signUpTab);

		dataPanel = new JPanel();
		dataPanel.add(tabs);

		content.add(dataPanel, "login");
		content.add(chatPanel, "chat");

		cl.show(content, "login");
	}

	private JPanel createLogInPanel() {
		JPanel toReturn = new JPanel();
		toReturn.setLayout(new BorderLayout());
		JPanel box = new JPanel();
		box.setLayout(new BoxLayout(box, BoxLayout.PAGE_AXIS));
		Border padding = BorderFactory.createEmptyBorder(60, 150, 20, 150);
		box.setBorder(padding);

		box.add(new JLabel("Username"));
		box.add(Box.createVerticalStrut(10));
		username = new JTextField();
		username.setPreferredSize(new Dimension(200, 20));
		box.add(username);
		box.add(Box.createVerticalStrut(30));

		box.add(new JLabel("Password"));
		box.add(Box.createVerticalStrut(10));
		password = new JTextField();
		password.setPreferredSize(new Dimension(200, 20));
		box.add(password);
		box.add(Box.createVerticalStrut(80));

		logInButton = new JButton("Log-In");
		box.add(logInButton);
		logInButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				boolean connected = client.logIn(username.getText(), password.getText());
				if (connected) {
					dataPanel.setVisible(false);
					chatPanel.setVisible(true);
					input.requestFocus();
					client.startListen();
				} else {
					// TODO set labels with user and pass wrong
				}

			}
		});

		toReturn.add(box, BorderLayout.PAGE_START);
		toReturn.add(logInButton, BorderLayout.PAGE_END);

		return toReturn;
	}

	private JPanel createChatPanel() {
		JPanel toReturnChat = new JPanel();

		textArea = new JTextArea();
		textArea.setPreferredSize(new Dimension(300, 300));
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		toReturnChat.add(textArea);

		input = new JTextField();
		input.setPreferredSize(new Dimension(200, 20));
		input.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				String text = input.getText();
				if (!text.equals("")) {
					buildMessage(text);
				}
				input.setText("");
			}
		});
		toReturnChat.add(input);

		return toReturnChat;

	}

	private void initGUI() {

		try {
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		initFrame();

		initPanels();

		frame.add(content);
		frame.setVisible(true);
	}

	public synchronized void buildMessage(String s) {
		toSend += s;
	}

	public synchronized void getMessage() {
		while (!client.messages.isEmpty()) {
			Packet message = client.messages.poll();
			incoming += message.getSender() + ": " + (String) message.data + "\n";
		}
	}

	private void sendMessage() {
		if (!toSend.equals("")) {
			client.send(toSend);
			toSend = "";
		}
	}

	@Override
	public void run() {
		textArea.append(incoming);
		incoming = "";
	}

	public static void main(String[] args) throws IOException {

		Client client = new Client();
		ClientGUI gui = new ClientGUI(client);
		client.connect(Client.HOST, Client.PORT);
		gui.initGUI();

		while (true) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			gui.sendMessage();
			gui.getMessage();
			SwingUtilities.invokeLater(gui);
		}

	}

}
