package client;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Map;

import javax.swing.DefaultListModel;
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
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.SoftBevelBorder;

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

	private JPanel statusLinePanel;
	private JLabel statusLine;

	private JTextField nameToFind;

	private GroupLayout groupLayout;

	private DefaultListModel model;

	public static KnownHostsManager hostsManager = new KnownHostsManager();

	private String getPortValue() {
		return lblPortValue.getText();
	}

	private String getNicknameValue() {
		return lblNameValue.getText();
	}

	public void setPortValue(String port) {
		lblPortValue.setText(port);
	}

	public void setNicknameValue(String nickname) {
		lblNameValue.setText(nickname);
	}

	private Server server;
	private JScrollPane scrollPane;

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
		
		scrollPane = new JScrollPane();
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
		groupLayout.setHorizontalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
					.addComponent(statusLinePanel, GroupLayout.DEFAULT_SIZE, 523, Short.MAX_VALUE)
					.addGroup(groupLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(btnClose, GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(btnSendName, GroupLayout.PREFERRED_SIZE, 342, GroupLayout.PREFERRED_SIZE)
						.addContainerGap())
					.addGroup(groupLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
							.addComponent(lblJutukas, Alignment.LEADING)
							.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
								.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
									.addComponent(lblPort)
									.addComponent(lblName))
								.addGap(18)
								.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
									.addComponent(lblNameValue)
									.addComponent(lblPortValue)))
							.addGroup(Alignment.LEADING, groupLayout.createParallelGroup(Alignment.TRAILING, false)
								.addComponent(btnSettings, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(btnConnect, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
									.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
										.addComponent(lblStatus)
										.addComponent(lblIp))
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
										.addComponent(lblIpValue)
										.addComponent(lblStatusValue)))))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
							.addComponent(btnAskNames, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
							.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)
							.addComponent(lblKnownUsers)
							.addGroup(groupLayout.createSequentialGroup()
								.addComponent(btnFindName)
								.addGap(4)
								.addComponent(nameToFind, GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)))
						.addContainerGap())
			);
	}

	/**
	 * Sets vertical group for the GroupLayout. This method is a true evil, try
	 * not to read it.
	 * 
	 * @param groupLayout
	 *            - layout.
	 */
	private void initializeVerticalGroup(GroupLayout groupLayout) {
		groupLayout.setVerticalGroup(
				groupLayout.createParallelGroup(Alignment.TRAILING)
					.addGroup(groupLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
							.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
								.addComponent(lblJutukas)
								.addGap(5)
								.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
									.addComponent(lblStatus)
									.addComponent(lblStatusValue))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
									.addComponent(lblIp)
									.addComponent(lblIpValue))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
									.addGroup(groupLayout.createSequentialGroup()
										.addComponent(lblPort)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(lblName))
									.addGroup(groupLayout.createSequentialGroup()
										.addComponent(lblPortValue)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(lblNameValue)))
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
									.addComponent(btnConnect)
									.addComponent(btnAskNames)))
							.addGroup(groupLayout.createSequentialGroup()
								.addComponent(lblKnownUsers)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
								.addGap(41)))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
							.addComponent(nameToFind)
							.addComponent(btnSettings, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(btnFindName, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
							.addComponent(btnClose)
							.addComponent(btnSendName))
						.addPreferredGap(ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
						.addComponent(statusLinePanel, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE))
			);
			knownUsersList = new JList(model);
			scrollPane.setViewportView(knownUsersList);
			knownUsersList.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null,
					null));
	}

	private void createKnownUsersList() {
		lblKnownUsers = new JLabel("Known users:");

		model = new DefaultListModel();
		checkKnownUsers();
	}

	private void checkKnownUsers() {
		for (Map.Entry<String, String> entry : hostsManager
				.getMapOfKnownHosts().entrySet()) {
			if (!model.contains(entry.getKey())) {
				model.addElement(entry.getKey());
			}
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
//		Scanner inputReader = null;
//		PrintWriter pw = null;
//		String content = "";
//		try {
//			inputReader = new Scanner(new File("known_hosts.txt"));
//			inputReader.useDelimiter("],");
//			inputReader.next();
//			inputReader.useDelimiter("\\Z");
//			content = inputReader.next();
//			System.out.println(content);
//		} catch (FileNotFoundException e1) {
//			System.out.println("Error opening file!!!");
//		} finally {
//			inputReader.close();
//		}
//
//		try {
//			pw = new PrintWriter(new File("known_hosts.txt"));
//			pw.write("[\n  [\n    \"" + lblNameValue.getText() + "\",\n    \""
//					+ lblIpValue.getText() + ":" + lblPortValue.getText()
//					+ "\"\n  ],\n" + content.substring(3, content.length()));
//		} catch (IOException e) {
//			System.out.println("Error writing to file!!!");
//		} finally {
//			pw.close();
//		}
		hostsManager.replaceFirstEntryInFile(getNicknameValue(), lblIpValue.getText() + ":" + getPortValue());
	}

	/**
	 * Create start button.
	 */
	private void createStartButton() {
		btnConnect = new JButton("Connect");
		btnConnect.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				appendNameToFile();
				checkKnownUsers();
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
		btnClose.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
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
		btnSettings.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
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
