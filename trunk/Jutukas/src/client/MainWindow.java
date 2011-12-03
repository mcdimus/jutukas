package client;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import server.Server;

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
	private JScrollPane scrollPane;

	private JPanel statusLinePanel;
	private JLabel statusLine;

	private JTextField nameToFind;

	private GroupLayout groupLayout;

	private Server server;

	public static KnownHostsManager hostsManager = new KnownHostsManager();
	public static HashMap<String, ChatWindow> chatWindowsMap = new HashMap<String, ChatWindow>();

	private String getPortValue() {
		return lblPortValue.getText();
	}

	public String getNicknameValue() {
		return lblNameValue.getText();
	}

	public void setPortValue(String port) {
		lblPortValue.setText(port);
	}

	public void setNicknameValue(String nickname) {
		lblNameValue.setText(nickname);
		appendNameToFile();
		addKnownUsers();
		knownUsersList.setListData(knownHosts);
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
					new ChatWindow();
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
		frame.setSize(526, 301);
		frame.setResizable(false);
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage("chat.png"));
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
						.createParallelGroup(Alignment.LEADING)
						.addComponent(statusLinePanel,
								GroupLayout.DEFAULT_SIZE, 523, Short.MAX_VALUE)
						.addGroup(
								groupLayout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(btnClose,
												GroupLayout.DEFAULT_SIZE, 145,
												Short.MAX_VALUE)
										.addPreferredGap(
												ComponentPlacement.UNRELATED)
										.addComponent(btnSendName,
												GroupLayout.PREFERRED_SIZE,
												342, GroupLayout.PREFERRED_SIZE)
										.addContainerGap())
						.addGroup(
								groupLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												groupLayout
														.createParallelGroup(
																Alignment.TRAILING)
														.addComponent(
																lblJutukas,
																Alignment.LEADING)
														.addGroup(
																Alignment.LEADING,
																groupLayout
																		.createSequentialGroup()
																		.addGroup(
																				groupLayout
																						.createParallelGroup(
																								Alignment.LEADING)
																						.addComponent(
																								lblPort)
																						.addComponent(
																								lblName))
																		.addGap(18)
																		.addGroup(
																				groupLayout
																						.createParallelGroup(
																								Alignment.LEADING)
																						.addComponent(
																								lblNameValue)
																						.addComponent(
																								lblPortValue)))
														.addGroup(
																Alignment.LEADING,
																groupLayout
																		.createParallelGroup(
																				Alignment.TRAILING,
																				false)
																		.addComponent(
																				btnSettings,
																				Alignment.LEADING,
																				GroupLayout.DEFAULT_SIZE,
																				GroupLayout.DEFAULT_SIZE,
																				Short.MAX_VALUE)
																		.addComponent(
																				btnConnect,
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
																												lblIp))
																						.addPreferredGap(
																								ComponentPlacement.UNRELATED)
																						.addGroup(
																								groupLayout
																										.createParallelGroup(
																												Alignment.LEADING)
																										.addComponent(
																												lblIpValue)
																										.addComponent(
																												lblStatusValue)))))
										.addPreferredGap(
												ComponentPlacement.RELATED)
										.addGroup(
												groupLayout
														.createParallelGroup(
																Alignment.LEADING)
														.addComponent(
																btnAskNames,
																Alignment.TRAILING,
																GroupLayout.DEFAULT_SIZE,
																340,
																Short.MAX_VALUE)
														.addComponent(
																scrollPane,
																GroupLayout.DEFAULT_SIZE,
																308,
																Short.MAX_VALUE)
														.addComponent(
																lblKnownUsers)
														.addGroup(
																groupLayout
																		.createSequentialGroup()
																		.addComponent(
																				btnFindName)
																		.addGap(4)
																		.addComponent(
																				nameToFind,
																				GroupLayout.DEFAULT_SIZE,
																				229,
																				Short.MAX_VALUE)))
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
																Alignment.TRAILING,
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
																								Alignment.TRAILING)
																						.addGroup(
																								groupLayout
																										.createSequentialGroup()
																										.addComponent(
																												lblPort)
																										.addPreferredGap(
																												ComponentPlacement.RELATED)
																										.addComponent(
																												lblName))
																						.addGroup(
																								groupLayout
																										.createSequentialGroup()
																										.addComponent(
																												lblPortValue)
																										.addPreferredGap(
																												ComponentPlacement.RELATED)
																										.addComponent(
																												lblNameValue)))
																		.addPreferredGap(
																				ComponentPlacement.UNRELATED)
																		.addGroup(
																				groupLayout
																						.createParallelGroup(
																								Alignment.BASELINE)
																						.addComponent(
																								btnConnect)
																						.addComponent(
																								btnAskNames)))
														.addGroup(
																groupLayout
																		.createSequentialGroup()
																		.addComponent(
																				lblKnownUsers)
																		.addPreferredGap(
																				ComponentPlacement.RELATED)
																		.addComponent(
																				scrollPane,
																				GroupLayout.PREFERRED_SIZE,
																				100,
																				GroupLayout.PREFERRED_SIZE)
																		.addGap(41)))
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
																btnSettings,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addComponent(
																btnFindName,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE))
										.addPreferredGap(
												ComponentPlacement.RELATED)
										.addGroup(
												groupLayout
														.createParallelGroup(
																Alignment.LEADING)
														.addComponent(btnClose)
														.addComponent(
																btnSendName))
										.addPreferredGap(
												ComponentPlacement.RELATED, 20,
												Short.MAX_VALUE)
										.addComponent(statusLinePanel,
												GroupLayout.PREFERRED_SIZE, 18,
												GroupLayout.PREFERRED_SIZE)));

	}

	private String[][] knownHosts;

	private void createKnownUsersList() {
		lblKnownUsers = new JLabel("Known users:");
		scrollPane = new JScrollPane();
		addKnownUsers();
		knownUsersList = new JList(knownHosts);
		scrollPane.setViewportView(knownUsersList);
		knownUsersList.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null,
				null));
		knownUsersList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					if (knownUsersList.getSelectedIndex() != -1
							&& !((String[]) knownUsersList.getSelectedValue())[0]
									.contains("ID") && !((String[]) knownUsersList.getSelectedValue())[1]
											.equals(lblNameValue.getText())) {
						// get name from string
						String[] selectedStringFromList = (String[]) knownUsersList
								.getSelectedValue();
						String name = selectedStringFromList[1];
						nameToFind.setText(name);
					}
				} else {
					// no selection
					nameToFind.setText("");
				}

			}
		});
		knownUsersList.setCellRenderer(new MyCellRenderer(MainWindow.this));
	}

	/**
	 * @author Aleksei Kulitskov
	 * 
	 */
	@SuppressWarnings("serial")
	private static class MyCellRenderer extends JPanel implements
			ListCellRenderer {
		JLabel idLabel, nameLabel, ipLabel;
		
		private MainWindow mainWindow;

		MyCellRenderer(MainWindow window) {
			this.mainWindow = window;
			setLayout(new GridLayout(1, 3));
			idLabel = new JLabel();
			nameLabel = new JLabel();
			ipLabel = new JLabel();
			add(idLabel);
			add(nameLabel);
			add(ipLabel);
		}

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			String leftData = ((String[]) value)[0];
			String middleData = ((String[]) value)[1];
			String rightData = ((String[]) value)[2];
			idLabel.setText(leftData);
			if(middleData.equals(mainWindow.getNicknameValue())) {
				nameLabel.setText("<html><FONT COLOR=RED>you: </FONT>" + middleData + "</html>");
			} else {
				nameLabel.setText(middleData);
			}
			ipLabel.setText(rightData);
			if (idLabel.getText().equals("ID")) {
				idLabel.setOpaque(false);
				nameLabel.setOpaque(false);
				ipLabel.setOpaque(false);
			} else {
				idLabel.setOpaque(true);
				nameLabel.setOpaque(true);
				ipLabel.setOpaque(true);
			}

			if (isSelected) {
				idLabel.setBackground(list.getSelectionBackground());
				idLabel.setForeground(list.getSelectionForeground());
				nameLabel.setBackground(list.getSelectionBackground());
				nameLabel.setForeground(list.getSelectionForeground());
				ipLabel.setBackground(list.getSelectionBackground());
				ipLabel.setForeground(list.getSelectionForeground());
			} else {
				idLabel.setBackground(list.getBackground());
				idLabel.setForeground(list.getForeground());
				nameLabel.setBackground(list.getBackground());
				nameLabel.setForeground(list.getForeground());
				ipLabel.setBackground(list.getBackground());
				ipLabel.setForeground(list.getForeground());
			}
			setEnabled(list.isEnabled());
			setFont(list.getFont());
			return this;
		}
	}

	private void addKnownUsers() {
		knownHosts = new String[hostsManager.getArrayFromJson().length + 1][3];
		int index = 1;
		knownHosts[0][0] = "ID";
		knownHosts[0][1] = "Name";
		knownHosts[0][2] = "IP";
		for (String[] host : hostsManager.getArrayFromJson()) {
			knownHosts[index][0] = String.valueOf(index) + ".";
			knownHosts[index][1] = host[0];
			knownHosts[index++][2] = host[1].split(":")[0];
		}
	}

	/**
	 * Create buttons.
	 */
	private void createButtons() {
		createStartButton();
		createStopButton();
		createSettingsButton();
		createAskNamesButton();
		createFindNameButtonWithTextField();
		createSendNameButton();
	}

	/**
	 * Append your nickname and IP to the first line.
	 */
	private void appendNameToFile() {
		hostsManager.replaceFirstEntryInFile(getNicknameValue(),
				lblIpValue.getText() + ":" + getPortValue());
	}

	/**
	 * Create start button.
	 */
	private void createStartButton() {
		btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				server = new Server(lblIpValue.getText(), getPortValue());
				statusLine.setText("Server is online.");
				lblStatusValue.setForeground(Color.GREEN);
				lblStatusValue.setText("running...");
				buttonsEnabler();

			}
		});
	}

	/**
	 * Enable buttons when clicked on Start button.
	 */
	private void buttonsEnabler() {
		btnConnect.setEnabled(false);
		btnSettings.setEnabled(false);
		btnClose.setEnabled(true);
		btnAskNames.setEnabled(true);
		btnFindName.setEnabled(true);
		btnSendName.setEnabled(true);
		nameToFind.setEnabled(true);
	}

	/**
	 * Disable buttons when clicked on Stop button.
	 */
	private void buttonsDisabler() {
		btnConnect.setEnabled(true);
		btnSettings.setEnabled(true);
		btnClose.setEnabled(false);
		btnAskNames.setEnabled(false);
		btnFindName.setEnabled(false);
		btnSendName.setEnabled(false);
		nameToFind.setEnabled(false);
	}

	/**
	 * Create stop button.
	 */
	private void createStopButton() {
		btnClose = new JButton("Stop");
		btnClose.setEnabled(false);
		btnClose.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				server.killServer();
				statusLine.setText("Press connect to start the server.");
				lblStatusValue.setForeground(Color.RED);
				lblStatusValue.setText("not running");
				buttonsDisabler();
			}

		});
	}

	/**
	 * Create settings button.
	 */
	private void createSettingsButton() {
		btnSettings = new JButton("Settings");
		btnSettings.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new SettingsWindow(getPortValue(), getNicknameValue(),
						MainWindow.this);
			}
		});
	}

	private void createAskNamesButton() {
		btnAskNames = new JButton("Ask names");
		btnAskNames.setEnabled(false);
	}

	private void createFindNameButtonWithTextField() {

		nameToFind = new JTextField();
		nameToFind.setColumns(10);
		nameToFind.setEnabled(false);

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
		lblStatusValue.setForeground(Color.RED);

		try {
			lblIpValue = new JLabel(getIpAddress());
		} catch (SocketException e) {
			System.err
					.println("Some shit happened with ip-address serching...");
		}

		String[] nameAndPortFromFile = hostsManager.getYourNameAndPort().split(
				";");

		lblPortValue = new JLabel(nameAndPortFromFile[1]);
		lblNameValue = new JLabel(nameAndPortFromFile[0]);
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

	/**
	 * Returns an ip-address in the current LAN. It can be eth* network or net*
	 * network.
	 * 
	 * @return an ip-address in the current LAN.
	 * @throws SocketException
	 *             - just let it be.
	 */
	private String getIpAddress() throws SocketException {
		String ipAddress = null;

		for (final Enumeration<NetworkInterface> interfaces = NetworkInterface
				.getNetworkInterfaces(); interfaces.hasMoreElements();) {

			final NetworkInterface cur = interfaces.nextElement();

			if (cur.isLoopback()) {
				continue;
			}

			// System.out.println("interface " + cur.getName());

			if (!(cur.getName().contains("eth")
					|| cur.getName().contains("wlan") || cur.getName()
					.contains("net"))) {
				continue;
			}
			for (final InterfaceAddress addr : cur.getInterfaceAddresses()) {
				final InetAddress inetAddr = addr.getAddress();

				if (!(inetAddr instanceof Inet4Address)) {
					continue;
				}
				ipAddress = inetAddr.getHostAddress();
				// System.out.println("  address: "
				// + inet_addr.getHostAddress() + "/"
				// + addr.getNetworkPrefixLength());
				//
				// System.out.println("  broadcast address: "
				// + addr.getBroadcast().getHostAddress());
			}
		}
		return ipAddress;

	}
}
