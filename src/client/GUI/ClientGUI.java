package client.GUI;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
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
import data.TYPE;

/**
 * @author Curcudel Ioan-Razvan
 */

public class ClientGUI implements Runnable {

	private static final int	WIDTH			= 540;
	private static final int	HEIGHT			= 400;

	private Client				client			= null;

	private String				toSend			= "";
	private TYPE				toSendType		= TYPE.MESSAGE;
	private String				incoming		= "";
	private String				incomingTrivia	= "";

	private JFrame				frame			= null;
	private CardLayout			cl				= null;

	private JPanel				content			= null;			// main panel
	private JPanel				dataPanel		= null;			// first panel
	private JPanel				chatPanel		= null;			// chat panel

	// #################### DATA PANEL ####################
	private JTabbedPane			tabs			= null;

	// ~~~~~~~~~~~~~~~~~~~~~ LogInTab ~~~~~~~~~~~~~~~~~~~~~
	private JPanel				logInTab		= null;
	private JTextField			username		= null;
	private JTextField			password		= null;
	private JButton				logInButton		= null;

	// ~~~~~~~~~~~~~~~~~~~~~ SignUpTab ~~~~~~~~~~~~~~~~~~~~
	private JPanel				signUpTab		= null;

	// private JPanel changePassTab = null; //TODO change pass

	// #################### CHAT PANEL ####################
	private JTextArea			chatTextArea	= null;
	private JTextField			chatInput		= null;
	private JButton				switchToTrivia	= null;

	// ################## TRIVIA DIALOG ###################
	private JDialog				triviaDialog	= null;
	private JTextArea			triviaTextArea	= null;
	private JTextField			triviaInput		= null;

	public ClientGUI(Client client) {
		this.client = client;
	}

	private void initFrame() {
		frame = new JFrame();
		frame.setBounds(0, 0, WIDTH, HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.addWindowListener(new WindowListener(){

			@Override
			public void windowOpened(WindowEvent e) {}

			@Override
			public void windowIconified(WindowEvent e) {}

			@Override
			public void windowDeiconified(WindowEvent e) {}

			@Override
			public void windowDeactivated(WindowEvent e) {}

			@Override
			public void windowClosed(WindowEvent e) {}

			@Override
			public void windowActivated(WindowEvent e) {}

			@Override
			public void windowClosing(WindowEvent e) {
				client.disconnect();
			}
		});
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
		box.add(Box.createVerticalStrut(40));

		JLabel wrongCredentials = new JLabel("");
		wrongCredentials.setSize(50, 20);
		box.add(wrongCredentials);
		box.add(Box.createVerticalStrut(20));

		logInButton = new JButton("Log-In");
		box.add(logInButton);
		logInButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				boolean connected = client.logIn(username.getText(), password.getText());
				if (connected) {
					dataPanel.setVisible(false);
					chatPanel.setVisible(true);
					chatInput.requestFocus();
					client.startListen();
				} else {
					// TODO set labels with user and pass wrong
					username.setText("");
					password.setText("");
					username.requestFocus();
					wrongCredentials.setText("Wrong user/password");
				}

			}
		});

		toReturn.add(box, BorderLayout.PAGE_START);
		toReturn.add(logInButton, BorderLayout.PAGE_END);

		return toReturn;
	}

	private JPanel createChatPanel() {
		JPanel toReturnChat = new JPanel();

		chatTextArea = new JTextArea();
		chatTextArea.setPreferredSize(new Dimension(300, 300));
		chatTextArea.setEditable(false);
		chatTextArea.setLineWrap(true);
		chatTextArea.setWrapStyleWord(true);
		toReturnChat.add(chatTextArea);

		chatInput = new JTextField();
		chatInput.setPreferredSize(new Dimension(200, 20));
		chatInput.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				String text = chatInput.getText();
				if (!text.equals("")) {
					if (text.startsWith("\\")) {
						buildMessage(text, TYPE.COMMAND);
					} else
						buildMessage(text, TYPE.MESSAGE);
				}
				chatInput.setText("");
			}
		});
		toReturnChat.add(chatInput);
		
		triviaDialog = createTriviaDialog();
		
		switchToTrivia = new JButton("trivia");
		switchToTrivia.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				triviaDialog.setVisible(true);
			}
		});
		toReturnChat.add(switchToTrivia);

		return toReturnChat;

	}

	
	private JDialog createTriviaDialog() {
		JDialog dialog = new JDialog(frame, "Trivia", false);
		JPanel panel = new JPanel();
		dialog.setLocationRelativeTo(chatPanel);
		dialog.setSize(500, 350);
		triviaTextArea = new JTextArea();
		triviaTextArea.setPreferredSize(new Dimension(250, 250));
		triviaTextArea.setEditable(false);
		triviaTextArea.setLineWrap(true);
		triviaTextArea.setWrapStyleWord(true);
		panel.add(triviaTextArea);

		triviaInput = new JTextField();
		triviaInput.setPreferredSize(new Dimension(200, 20));
		triviaInput.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				String text = triviaInput.getText();
				if (!text.equals(""))
					buildMessage(text, TYPE.TRIVIA);
				triviaInput.setText("");
			}
		});
		panel.add(triviaInput);

		dialog.add(panel);
		return dialog;
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

	public synchronized void buildMessage(String s, TYPE type) {
		toSend += s;
		toSendType = type;
	}

	public synchronized void getMessage() {
		while (!client.messages.isEmpty()) {
			Packet message = client.messages.poll();
			if (message.getType() == TYPE.MESSAGE) {
				// if server send the message, sender is null

				if (message.getSender() != null) {
					incoming += message.getSender() + ": " + (String) message.getData() + "\n";
				} else {
					incoming += (String) message.getData() + "\n";
				}
			}
			if (message.getType() == TYPE.TRIVIA) {
				incomingTrivia += "Trivia: " + (String) message.getData();
			}
		}
	}

	private void sendMessage() {
		if (!toSend.equals("")) {
			client.send(toSend, toSendType);
			toSend = "";
			toSendType = TYPE.MESSAGE;
		}
	}

	@Override
	public void run() {
		chatTextArea.append(incoming);
		incoming = "";
		triviaTextArea.append(incomingTrivia);
		incomingTrivia = "";
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
