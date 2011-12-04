package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ChatWindow {

	private JFrame frame;
	private JTextField textField;
	private final JEditorPane editorPane = new JEditorPane();
	private DefaultListModel listModel;

	/**
	 * Create the application.
	 */
	public ChatWindow() {
		initialize();
		frame.setVisible(true);
	}

	public synchronized void acceptMessage(String name, String ip,
			String message) {
		// TODO check name in the list: if exists just print out message; if not
		// add to list and print message)
		if (!listModel.contains(name + " - " + ip)) {
			listModel.addElement(name + " - " + ip);
		}
		appendText(message);
	}
	
	public void sendMessage() {
		
	}

	public synchronized void appendText(String text) {
		String editorPaneText = editorPane.getText();
		editorPane.setText(editorPaneText.split("</body>")[0] + "<br>" + text
				+ "</body>");

	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		// KnownHostsManager khm = new KnownHostsManager();
		// khm.getMapOfKnownHosts();
		// khm.addNewHosts("[[\"Tanel Tammet\",\"22.33.44.55:6766\"],[\"Dmitri Laud\",\"22.33.44.11:6666\"]]");
		frame = new JFrame();
		frame.setTitle("Jutukas");
		frame.setSize(800, 500);
		UIutils.centerFrameOnScreen(frame);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		createMenuBar();

		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.SOUTH);

		JLabel lblNewLabel = new JLabel("Message:");
		panel.add(lblNewLabel);

		textField = new JTextField();
		panel.add(textField);
		textField.setColumns(50);

		JButton btnNewButton = new JButton("Send");
		btnNewButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				appendText(textField.getText());
				textField.setText("");
			}
		});
		panel.add(btnNewButton);

		Box horizontalBox = Box.createHorizontalBox();
		panel.add(horizontalBox);

		editorPane.setContentType("text/html");
		editorPane.setText("<h1>Hello world</h1><br><i>blablalbalb</i>");
		editorPane.setEditable(false);

		frame.getContentPane().add(editorPane);

		JList list = new JList();
		list.setBorder(new LineBorder(Color.BLACK));
		listModel = new DefaultListModel();
		list.setModel(listModel);
		list.setMinimumSize(new Dimension(200, 200));
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		frame.getContentPane().add(list, BorderLayout.WEST);

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
