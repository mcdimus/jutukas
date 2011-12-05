package client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import server.Sender;
import server.Server;

public class ChatWindow {

	private JFrame frame;
	private JTextField textField;
	private final JEditorPane editorPane = new JEditorPane();
	private String opponentName;
	private String opponetnIp;
	private JButton sendButton;

	/**
	 * Create the application.
	 * 
	 * @param mainWindow
	 */
	public ChatWindow(String opponentName, String opponentIp) {
		this.opponentName = opponentName;
		this.opponetnIp = opponentIp;
		initialize();
	}

	public synchronized void acceptMessage(String name, String ip,
			String message) {
		appendText(name, message, "blue");
	}

	public void sendMessage() {
		String messageToSend = textField.getText();
		if (!messageToSend.isEmpty()) {
			appendText(Server.NAME, messageToSend, "green");
			textField.setText("");
			new Sender(opponentName, opponetnIp, messageToSend);
		}
	}

	private SimpleDateFormat dateFormatter = new SimpleDateFormat(
			"dd.MM.yyyy - HH:mm:ss");

	public synchronized void appendText(String name, String message,
			String color) {
		Date now = new Date();
		String editorPaneText = editorPane.getText();
		String text = "<html><font size=2 color=grey><i>"
				+ dateFormatter.format(now) + "</i></font><br><b><font color="
				+ color + ">" + name + "</font>:</b> " + message
				+ "<br></html>";
		editorPane.setText(editorPaneText.split("</body>")[0] + "<br>" + text
				+ "</body>");

	}

	public boolean isVisible() {
		return frame.isVisible();
	}

	public void setVisible(boolean visible) {
		frame.setVisible(visible);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		// KnownHostsManager khm = new KnownHostsManager();
		// khm.getMapOfKnownHosts();
		// khm.addNewHosts("[[\"Tanel Tammet\",\"22.33.44.55:6766\"],[\"Dmitri Laud\",\"22.33.44.11:6666\"]]");
		frame = new JFrame();
		frame.setTitle("Chatting with " + opponentName);
		frame.setSize(800, 500);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		createMenuBar();

		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.SOUTH);

		JLabel lblNewLabel = new JLabel("Message:");
		panel.add(lblNewLabel);

		textField = new JTextField();
		panel.add(textField);
		textField.setColumns(50);

		createSendButton();

		panel.add(sendButton);

		Box horizontalBox = Box.createHorizontalBox();
		panel.add(horizontalBox);

		editorPane.setContentType("text/html");
		editorPane.setEditable(false);

		JScrollPane scrollPane = new JScrollPane(editorPane);
		scrollPane.setAutoscrolls(true);
		// to get bottom visible, use
		frame.getContentPane().add(scrollPane);
	}

	private void createSendButton() {
		@SuppressWarnings("serial")
		Action buttonClick = new AbstractAction("buttonClick") {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				sendButton.doClick();
			}
		};
		sendButton = new JButton("Send");
		sendButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage();

			}
		});

		sendButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke("ENTER"), "buttonClick");
		sendButton.getActionMap().put("buttonClick", buttonClick);
	}

	private void createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		createMenuFile(menuBar);

		createMenuView(menuBar);

		createMenuHelp(menuBar);
	}

	@SuppressWarnings("serial")
	private void createMenuFile(JMenuBar menuBar) {
		JMenu menuFile = new JMenu("File");
		menuFile.setMnemonic('F');
		menuBar.add(menuFile);

		JMenuItem menuItemOpenLastSession = new JMenuItem(new AbstractAction(
				"Open last session") {

			@Override
			public void actionPerformed(ActionEvent arg0) {

			}
		});
		menuItemOpenLastSession.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_O, InputEvent.CTRL_MASK));
		menuFile.add(menuItemOpenLastSession);

		JMenuItem menuItemSaveSession = new JMenuItem(new AbstractAction(
				"Save session") {

			@Override
			public void actionPerformed(ActionEvent arg0) {

			}
		});
		menuItemSaveSession.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_S, InputEvent.CTRL_MASK));
		menuFile.add(menuItemSaveSession);

		JMenuItem menuItemQuit = new JMenuItem(new AbstractAction("Quit") {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
			}
		});
		menuItemQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
				InputEvent.CTRL_MASK));
		menuFile.add(menuItemQuit);
	}

	@SuppressWarnings("serial")
	private void createMenuView(JMenuBar menuBar) {
		JMenu menuView = new JMenu("View");
		menuView.setMnemonic('V');
		menuBar.add(menuView);

		JMenuItem menuItemClearChat = new JMenuItem(new AbstractAction(
				"Clear chat") {

			@Override
			public void actionPerformed(ActionEvent arg0) {

			}
		});
		menuView.add(menuItemClearChat);

	}

	@SuppressWarnings("serial")
	private void createMenuHelp(JMenuBar menuBar) {
		JMenu menuHelp = new JMenu("Help");
		menuBar.add(menuHelp);

		JMenuItem menuItemAbout = new JMenuItem(new AbstractAction("About...") {

			@Override
			public void actionPerformed(ActionEvent arg0) {

			}
		});
		menuHelp.add(menuItemAbout);

	}

}
