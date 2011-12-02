package client;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.SoftBevelBorder;

public class MainWindow {

	private JFrame frame;
	private JLabel lblJutukas;

	private JLabel lblStatus;
	private JLabel lblIp;
	private JLabel lblPort;
	private JLabel lblName;
	private JLabel lblStatusValue;
	private JLabel lblIpValue;
	private JLabel lblPortValue;
	private JLabel lblNameValue;

	private JButton btnAskNames;
	private JButton btnFindName;
	private JButton btnSendName;
	private JButton btnConnect;
	private JButton btnSettings;
	private JButton btnClose;

	private JLabel lblKnownUsers;
	private JList knownUsersList;

	private JPanel statusLinePanel;
	private JLabel statusLine;

	private JTextField nameToFind;
	
	private GroupLayout groupLayout;

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
		frame = new JFrame("Jutukas");
		frame.setSize(450, 300);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		UIutils.centerFrameOnScreen(frame);

		lblJutukas = new JLabel("JUTUKAS");
		lblJutukas.setFont(new Font("Dialog", Font.BOLD, 30));

		createPanelWithStatusLine();
		createInformationBlock();
		createButtons();
		createKnownUsersList();

		initializeLayout();
		
		frame.getContentPane().setLayout(groupLayout);
	}

	/**
	 * Initializes the group layout for the frame.
	 */
	private void initializeLayout() {
		groupLayout = new GroupLayout(frame.getContentPane());

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
						.createParallelGroup(Alignment.TRAILING)
						.addComponent(statusLinePanel,
								GroupLayout.DEFAULT_SIZE, 448, Short.MAX_VALUE)
						.addGroup(
								groupLayout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(lblJutukas)
										.addPreferredGap(
												ComponentPlacement.RELATED)
										.addGroup(
												groupLayout
														.createParallelGroup(
																Alignment.TRAILING)
														.addComponent(
																lblKnownUsers,
																Alignment.LEADING)
														.addComponent(
																knownUsersList,
																GroupLayout.DEFAULT_SIZE,
																265,
																Short.MAX_VALUE))
										.addContainerGap())
						.addGroup(
								Alignment.LEADING,
								groupLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												groupLayout
														.createParallelGroup(
																Alignment.TRAILING,
																false)
														.addComponent(
																btnClose,
																Alignment.LEADING,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addComponent(
																btnSettings,
																Alignment.LEADING,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addGroup(
																Alignment.LEADING,
																groupLayout
																		.createSequentialGroup()
																		.addGroup(
																				groupLayout
																						.createParallelGroup(
																								Alignment.LEADING)
																						.addComponent(
																								lblStatus)
																						.addComponent(
																								lblIp)
																						.addComponent(
																								lblPort)
																						.addComponent(
																								lblName))
																		.addPreferredGap(
																				ComponentPlacement.UNRELATED)
																		.addGroup(
																				groupLayout
																						.createParallelGroup(
																								Alignment.LEADING)
																						.addComponent(
																								lblNameValue)
																						.addComponent(
																								lblPortValue)
																						.addComponent(
																								lblIpValue)
																						.addComponent(
																								lblStatusValue)))
														.addComponent(
																btnConnect,
																Alignment.LEADING,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE))
										.addPreferredGap(
												ComponentPlacement.RELATED)
										.addGroup(
												groupLayout
														.createParallelGroup(
																Alignment.TRAILING)
														.addGroup(
																Alignment.LEADING,
																groupLayout
																		.createSequentialGroup()
																		.addComponent(
																				btnFindName)
																		.addPreferredGap(
																				ComponentPlacement.RELATED)
																		.addComponent(
																				nameToFind,
																				GroupLayout.DEFAULT_SIZE,
																				152,
																				Short.MAX_VALUE))
														.addComponent(
																btnAskNames,
																Alignment.LEADING,
																GroupLayout.DEFAULT_SIZE,
																265,
																Short.MAX_VALUE)
														.addComponent(
																btnSendName,
																GroupLayout.DEFAULT_SIZE,
																265,
																Short.MAX_VALUE))
										.addContainerGap()));
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
						.createParallelGroup(Alignment.TRAILING)
						.addGroup(
								groupLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												groupLayout
														.createParallelGroup(
																Alignment.LEADING)
														.addGroup(
																groupLayout
																		.createSequentialGroup()
																		.addComponent(
																				lblJutukas)
																		.addGap(5)
																		.addGroup(
																				groupLayout
																						.createParallelGroup(
																								Alignment.BASELINE)
																						.addComponent(
																								lblStatus)
																						.addComponent(
																								lblStatusValue))
																		.addPreferredGap(
																				ComponentPlacement.RELATED)
																		.addGroup(
																				groupLayout
																						.createParallelGroup(
																								Alignment.BASELINE)
																						.addComponent(
																								lblIp)
																						.addComponent(
																								lblIpValue))
																		.addPreferredGap(
																				ComponentPlacement.RELATED)
																		.addGroup(
																				groupLayout
																						.createParallelGroup(
																								Alignment.BASELINE)
																						.addComponent(
																								lblPort)
																						.addComponent(
																								lblPortValue))
																		.addPreferredGap(
																				ComponentPlacement.RELATED)
																		.addGroup(
																				groupLayout
																						.createParallelGroup(
																								Alignment.BASELINE)
																						.addComponent(
																								lblName)
																						.addComponent(
																								lblNameValue)))
														.addGroup(
																groupLayout
																		.createSequentialGroup()
																		.addComponent(
																				lblKnownUsers)
																		.addGap(7)
																		.addComponent(
																				knownUsersList,
																				GroupLayout.PREFERRED_SIZE,
																				100,
																				GroupLayout.PREFERRED_SIZE)))
										.addGap(18)
										.addGroup(
												groupLayout
														.createParallelGroup(
																Alignment.LEADING)
														.addGroup(
																Alignment.TRAILING,
																groupLayout
																		.createSequentialGroup()
																		.addComponent(
																				btnConnect)
																		.addPreferredGap(
																				ComponentPlacement.RELATED)
																		.addComponent(
																				btnSettings)
																		.addPreferredGap(
																				ComponentPlacement.RELATED,
																				16,
																				Short.MAX_VALUE)
																		.addGroup(
																				groupLayout
																						.createParallelGroup(
																								Alignment.BASELINE)
																						.addComponent(
																								btnClose)
																						.addComponent(
																								btnSendName)))
														.addGroup(
																groupLayout
																		.createSequentialGroup()
																		.addComponent(
																				btnAskNames)
																		.addPreferredGap(
																				ComponentPlacement.RELATED)
																		.addGroup(
																				groupLayout
																						.createParallelGroup(
																								Alignment.LEADING,
																								false)
																						.addComponent(
																								nameToFind)
																						.addComponent(
																								btnFindName))))
										.addPreferredGap(
												ComponentPlacement.RELATED)
										.addComponent(statusLinePanel,
												GroupLayout.PREFERRED_SIZE, 18,
												GroupLayout.PREFERRED_SIZE)));
	}

	private void createKnownUsersList() {
		lblKnownUsers = new JLabel("Known users:");

		knownUsersList = new JList();
		knownUsersList.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null,
				null));
	}

	private void createButtons() {
		createStartButton();
		createStopButton();
		createSettingsButton();
		createAskNamesButton();
		createFindNameButtonWithTextField();
		createSendNameButton();
	}

	private void createStartButton() {
		btnConnect = new JButton("Start");
	}

	private void createStopButton() {
		btnClose = new JButton("Stop");
		btnClose.setEnabled(false);
	}

	private void createSettingsButton() {
		btnSettings = new JButton("Settings");
	}

	private void createAskNamesButton() {
		btnAskNames = new JButton("Ask names");
		btnAskNames.setEnabled(false);
	}

	private void createFindNameButtonWithTextField() {

		nameToFind = new JTextField();
		nameToFind.setColumns(10);

		btnFindName = new JButton("Find name");
		btnFindName.setEnabled(false);
	}

	private void createSendNameButton() {
		btnSendName = new JButton("Send name");
		btnSendName.setEnabled(false);
	}

	/**
	 * Creates information block with labels.
	 * 
	 * <pre>
	 * Status: [running, not running]
	 * IP:     [x.x.x.x]
	 * Port:   [xxxx]
	 * Name:   [kasutaja]
	 * </pre>
	 */
	private void createInformationBlock() {
		lblStatus = new JLabel("Status:");
		lblIp = new JLabel("IP:");
		lblPort = new JLabel("Port:");
		lblName = new JLabel("Name:");
		lblStatusValue = new JLabel("not running");
		lblIpValue = new JLabel("127.0.0.1");
		lblPortValue = new JLabel("6666");
		lblNameValue = new JLabel("kasutaja");
	}

	/**
	 * Creates panel with status line in the frame`s bottom.
	 */
	private void createPanelWithStatusLine() {
		statusLinePanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) statusLinePanel.getLayout();
		flowLayout.setVgap(0);
		flowLayout.setAlignment(FlowLayout.LEFT);
		statusLinePanel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED,
				null, null, null, null));

		statusLine = new JLabel("Press connect to start the server.");
		statusLine.setVerticalAlignment(SwingConstants.TOP);
		statusLinePanel.add(statusLine);
	}
}
