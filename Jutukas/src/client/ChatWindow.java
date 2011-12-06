package client;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import server.Sender;
import server.Server;

public class ChatWindow {

	private JFrame frame;
	private JTextField textField;
	private JEditorPane editorPane;
	private String opponentName;
	private String opponetnIp;
	private JButton sendButton;
	private final JButton smileysBtn = new JButton("");
	private final JPopupMenu smileysMenu = new JPopupMenu();
	JPanel bottomPanel;
	private static HashMap<String, String> smileysToImages = new HashMap<String, String>();
	private static String[] smileys = { "biggrin.gif", "smile.gif", "sad.gif",
			"bye.gif", "wink.gif", "tongue.gif", "shy.gif", "dry.gif",
			"cool.gif", "mad.gif", "rolleyes.gif", "blink.gif" };
	private static String[] smileysText = { ":D", ":)", ":(", ":B", ";)", ":P",
			":H", ":S", ":C", "x-(", ":O", "o_O" };

	/**
	 * Create the application.
	 * 
	 * @param mainWindow
	 */
	public ChatWindow(String opponentName, String opponentIp) {
		this.opponentName = opponentName;
		this.opponetnIp = opponentIp;
		smileysToMap();
		initialize();
	}

	/**
	 * Turn smileys' text into Map.
	 */
	private void smileysToMap() {
		String value;
		String key;
		for (int i = 0; i < smileys.length; i++) {
			key = smileysText[i];
			value = smileys[i];
			smileysToImages.put(key, value);
		}
	}

	/**
	 * Replaces all the smiley's text into HTML in the specified message.
	 * 
	 * @param message
	 *            - message to replace.
	 * @return - replaced message with HTML tags.
	 */
	private String replaceTextSmileysToIcons(String message) {
		String replacedMessage = message;
		String imgsrc;
		String replacableString;
		for (Map.Entry<String, String> entry : smileysToImages.entrySet()) {
			if (replacedMessage.contains(entry.getKey())) {
				imgsrc = ClassLoader.getSystemResource(
						"smileys/" + entry.getValue()).toString();
				replacableString = "<img src='" + imgsrc + "'></img>";
				replacedMessage = replacedMessage.replace(entry.getKey(),
						replacableString);
			}
		}
		return replacedMessage;
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
				+ color + ">" + name + "</font>:</b> "
				+ replaceTextSmileysToIcons(message) + "<br></html>";
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
		frame = new JFrame();
		frame.setTitle("Chatting with " + opponentName);
		frame.setSize(800, 500);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		createSmileysPopUpMenu();

		createMenuBar();

		createEditorPane();

		createBottomPanel();

	}

	private void createSmileysPopUpMenu() {
		smileysMenu.setLayout(new GridLayout(4, 3));
		ImageIcon icon;
		JMenuItem smileyItem;
		URL imgsrc;
		for (final Map.Entry<String, String> entry : smileysToImages.entrySet()) {
			imgsrc = ClassLoader.getSystemResource("smileys/"
					+ entry.getValue());
			icon = new ImageIcon(imgsrc);
			smileyItem = new JMenuItem();
			smileyItem.setIcon(icon);
			smileyItem.addActionListener(new AbstractAction() {

				/**
				 * Serial ID.
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent arg0) {
					textField.setText(textField.getText() + " "
							+ entry.getKey());
					smileysMenu.setVisible(false);

				}
			});

			smileysMenu.add(smileyItem);
		}
	}

	private void createEditorPane() {
		editorPane = new JEditorPane();
		editorPane.setContentType("text/html");
		editorPane.setEditable(false);

		JScrollPane scrollPane = new JScrollPane(editorPane);
		scrollPane.setAutoscrolls(true);
		frame.getContentPane().add(scrollPane);
	}

	private void createBottomPanel() {
		bottomPanel = new JPanel();

		bottomPanel.setLayout(new BorderLayout());

		createToolBar();
		JPanel panel = new JPanel();
		JLabel lblNewLabel = new JLabel("Message:");
		panel.add(lblNewLabel);

		textField = new JTextField();
		panel.add(textField);
		textField.setColumns(50);

		createSmilesButton();
		panel.add(smileysBtn);
		createSendButton();
		panel.add(sendButton);

		// Box horizontalBox = Box.createHorizontalBox();
		// panel.add(horizontalBox);

		bottomPanel.add(panel, BorderLayout.SOUTH);
		frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
	}

	private void createToolBar() {
		JToolBar toolBar = new JToolBar("toolbar");
		toolBar.setFloatable(false);

		JButton btnB = new JButton("");
		btnB.setIcon(new ImageIcon(ChatWindow.class
				.getResource("/img/bold.png")));

		toolBar.add(btnB);

		JButton btnI = new JButton("");
		btnI.setIcon(new ImageIcon(ChatWindow.class
				.getResource("/img/italic.png")));
		toolBar.add(btnI);

		JButton btnU = new JButton("");
		btnU.setIcon(new ImageIcon(ChatWindow.class
				.getResource("/img/underline.png")));
		toolBar.add(btnU);

		bottomPanel.add(toolBar, BorderLayout.NORTH);
	}

	private void createSmilesButton() {
		smileysBtn.setToolTipText("Smileys");
		smileysBtn.setIcon(new ImageIcon(ChatWindow.class
				.getResource("/smileys/smile.gif")));
		smileysBtn.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				smileysMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		});
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
