package client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

public class MainWindow {

	private JFrame frame;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
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
		panel.add(btnNewButton);

		Box horizontalBox = Box.createHorizontalBox();
		panel.add(horizontalBox);

		JSplitPane splitPane = new JSplitPane();
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);

		JScrollPane scrollPane = new JScrollPane();
		splitPane.setLeftComponent(scrollPane);

		Component horizontalStrut = Box.createHorizontalStrut(200);
		scrollPane.setViewportView(horizontalStrut);

		final JEditorPane dtrpnhelloWorld = new JEditorPane();
		dtrpnhelloWorld.setContentType("text/html");
		dtrpnhelloWorld.setText("<h1>Hello world</h1>");
		dtrpnhelloWorld.setEditable(false);
		splitPane.setRightComponent(dtrpnhelloWorld);
		
		btnNewButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				dtrpnhelloWorld.setText(textField.getText());
			}
		});
	}

	@SuppressWarnings("serial")
	private void createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		createMenuFile(menuBar);

		createMenuEdit(menuBar);

		createMenuView(menuBar);

		createMenuHelp(menuBar);
	}

	@SuppressWarnings("serial")
	private void createMenuFile(JMenuBar menuBar) {
		JMenu menuFile = new JMenu("File");
		menuFile.setMnemonic('F');
		menuBar.add(menuFile);

		JMenuItem menuItemOpenLastSession = new JMenuItem("Open last session");
		menuItemOpenLastSession.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_O, InputEvent.CTRL_MASK));
		menuFile.add(menuItemOpenLastSession);

		JMenuItem menuItemSaveSession = new JMenuItem("Save session");
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

	private void createMenuEdit(JMenuBar menuBar) {
		JMenu menuEdit = new JMenu("Edit");
		menuEdit.setMnemonic('E');
		menuBar.add(menuEdit);

		JMenuItem menuItemFindUser = new JMenuItem("Find user...");
		menuItemFindUser.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
				InputEvent.CTRL_MASK));
		menuEdit.add(menuItemFindUser);

		JMenuItem menuItemKnownUsersList = new JMenuItem("Known users list");
		menuItemKnownUsersList.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_L, InputEvent.CTRL_MASK));
		menuEdit.add(menuItemKnownUsersList);

		JMenuItem menuItemUpdateUsersList = new JMenuItem("Update users list");
		menuItemUpdateUsersList.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_U, InputEvent.CTRL_MASK));
		menuEdit.add(menuItemUpdateUsersList);
	}

	private void createMenuView(JMenuBar menuBar) {
		JMenu menuView = new JMenu("View");
		menuView.setMnemonic('V');
		menuBar.add(menuView);

		// ButtonGroup for radio buttons
		ButtonGroup viewButtonGroup = new ButtonGroup();

		JRadioButtonMenuItem radioButtonMenuItemHideSidePane = new JRadioButtonMenuItem(
				"Hide side pane");
		menuView.add(radioButtonMenuItemHideSidePane);
		viewButtonGroup.add(radioButtonMenuItemHideSidePane);

		JRadioButtonMenuItem radioButtonMenuItemNewRadioItem = new JRadioButtonMenuItem(
				"Show online users");
		radioButtonMenuItemNewRadioItem.setSelected(true);
		menuView.add(radioButtonMenuItemNewRadioItem);
		viewButtonGroup.add(radioButtonMenuItemNewRadioItem);

		JRadioButtonMenuItem radioButtonMenuItemShowAllUsers = new JRadioButtonMenuItem(
				"Show all users");
		menuView.add(radioButtonMenuItemShowAllUsers);
		viewButtonGroup.add(radioButtonMenuItemShowAllUsers);

		JSeparator separator = new JSeparator();
		menuView.add(separator);

		JMenuItem menuItemClearChat = new JMenuItem("Clear chat");
		menuView.add(menuItemClearChat);

	}

	private void createMenuHelp(JMenuBar menuBar) {
		JMenu menuHelp = new JMenu("Help");
		menuBar.add(menuHelp);

		JMenuItem menuItemAbout = new JMenuItem("About...");
		menuHelp.add(menuItemAbout);

	}
}
