package client;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle.ComponentPlacement;

public class SettingsWindow {

	private JFrame settingsFrame;
	private JTextField nicknameTextField;
	private JTextField portTextField;
	private JLabel nicknameLabel;
	private JLabel portLabel;
	private JButton saveButton;
	private JButton cancelButton;
	private GroupLayout groupLayout;
	private String portValue;
	private String nicknameValue;
	private MainWindow parentWindow;

	/**
	 * Create the application.
	 */
	public SettingsWindow(String port, String nickname, MainWindow mainWindow) {
		this.parentWindow = mainWindow;
		this.portValue = port;
		this.nicknameValue = nickname;
		initialize();
		settingsFrame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		settingsFrame = new JFrame();
		settingsFrame.setResizable(false);
		settingsFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(
				"settings.png"));
		settingsFrame.setTitle("Settings");
		settingsFrame.setSize(236, 226);
		settingsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		UIutils.centerFrameOnScreen(settingsFrame);

		createLabels();
		createButtons();
		createTextFields();

		initializeLayout();

		settingsFrame.getContentPane().setLayout(groupLayout);
	}

	private void createLabels() {
		createNicknameLabel();
		createPortLabel();
	}

	private void createNicknameLabel() {
		nicknameLabel = new JLabel("Nickname:");
	}

	private void createPortLabel() {
		portLabel = new JLabel("Port:");
	}

	private void createTextFields() {
		createNicknameTextField();
		createPortTextField();
	}

	private void createNicknameTextField() {
		nicknameTextField = new JTextField();
		nicknameTextField.setColumns(10);
		nicknameTextField.setText(nicknameValue);
	}

	private void createPortTextField() {
		portTextField = new JTextField();
		portTextField.setColumns(10);
		portTextField.setText(portValue);
	}

	private void createButtons() {
		createSaveButton();
		createCancelButton();
	}
	
	  @SuppressWarnings("serial")
	private Action buttonClick = new AbstractAction("buttonClick") {
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			saveButton.doClick();			
		}
	};

	private void createSaveButton() {
		saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				parentWindow.setNicknameValue(nicknameTextField.getText());
				parentWindow.setPortValue(portTextField.getText());
				parentWindow.appendNameToFile();
				settingsFrame.dispose();

			}
		});

		saveButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "buttonClick");  
        saveButton.getActionMap().put("buttonClick", buttonClick);  
	}
	

	private void createCancelButton() {
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				settingsFrame.dispose();

			}
		});
	}

	/**
	 * Initializes the group layout for the frame.
	 */
	private void initializeLayout() {
		groupLayout = new GroupLayout(settingsFrame.getContentPane());

		initializeHorizontalGroup(groupLayout);
		initializeVerticalGroup(groupLayout);
	}

	/**
	 * Sets horizontal group for the GroupLayout. This method is a true evil,
	 * try not to read it.
	 * 
	 * @param groupLayout
	 *            - layout.
	 */
	private void initializeHorizontalGroup(GroupLayout groupLayout) {
		groupLayout
				.setHorizontalGroup(groupLayout
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								groupLayout
										.createSequentialGroup()
										.addGap(45)
										.addGroup(
												groupLayout
														.createParallelGroup(
																Alignment.LEADING)
														.addGroup(
																groupLayout
																		.createSequentialGroup()
																		.addComponent(
																				saveButton)
																		.addPreferredGap(
																				ComponentPlacement.UNRELATED)
																		.addComponent(
																				cancelButton))
														.addGroup(
																groupLayout
																		.createSequentialGroup()
																		.addGroup(
																				groupLayout
																						.createParallelGroup(
																								Alignment.LEADING)
																						.addComponent(
																								nicknameLabel)
																						.addComponent(
																								portLabel))
																		.addPreferredGap(
																				ComponentPlacement.UNRELATED)
																		.addGroup(
																				groupLayout
																						.createParallelGroup(
																								Alignment.LEADING)
																						.addComponent(
																								portTextField,
																								GroupLayout.PREFERRED_SIZE,
																								69,
																								GroupLayout.PREFERRED_SIZE)
																						.addComponent(
																								nicknameTextField,
																								GroupLayout.PREFERRED_SIZE,
																								69,
																								GroupLayout.PREFERRED_SIZE))))
										.addContainerGap(73, Short.MAX_VALUE)));
	}

	/**
	 * Sets vertical group for the GroupLayout. This method is a true evil, try
	 * not to read it.
	 * 
	 * @param groupLayout
	 *            - layout.
	 */
	private void initializeVerticalGroup(GroupLayout groupLayout) {
		groupLayout
				.setVerticalGroup(groupLayout
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								groupLayout
										.createSequentialGroup()
										.addGap(37)
										.addGroup(
												groupLayout
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																nicknameLabel)
														.addComponent(
																nicknameTextField,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
										.addGap(18)
										.addGroup(
												groupLayout
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(portLabel)
														.addComponent(
																portTextField,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
										.addGap(34)
										.addGroup(
												groupLayout
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																saveButton)
														.addComponent(
																cancelButton))
										.addContainerGap(36, Short.MAX_VALUE)));
	}
}
