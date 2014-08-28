package GASS.client.view;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextArea;

import test.SSHKit;
import GASS.utils.CryptoKit;
import dataType.AFile;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.SwingConstants;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

public class ClientMainUI extends JFrame {
	private static final long serialVersionUID = 1955985721542331548L;
	
	ClientV2Madapter v2madpter;
	
	private JPanel contentPane;
	private final JPanel pnlController1 = new JPanel();
	private final JPanel pnlDisplayInfo = new JPanel();
	private final JPanel panel_3 = new JPanel();
	private final JPanel panel_2 = new JPanel();
	private final JButton btnDelete = new JButton("Delete");
	private final JButton btnUpload = new JButton("Upload");
	private final JPanel panel_5 = new JPanel();
	private final JButton btnMembership = new JButton("Members");
	private final JButton btnLog = new JButton("LOG");
	private final JList<String> folderList = new JList<String>();
	private final JButton btnDownload = new JButton("Download");
	private final JButton btnRefresh = new JButton("Refresh");
	private final JTextArea textArea = new JTextArea();
	private ClientLogUI window;
	private final JButton btnJoinGroup = new JButton("Join group");
	private ClientMemberUI memberUI=new ClientMemberUI();
	
	private static final String IPv4PATTERN = 
	        "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
	        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
	        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
	        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
	private final JButton btnLeaveGroup = new JButton("Quit group");
	private final JButton btnClose = new JButton("Close");
	private final JLabel lblToQuitThe = new JLabel("       Quit the group:");
	private final JLabel lblOperation = new JLabel("Operation:");
	private final JScrollPane scrollPane = new JScrollPane();

	boolean validateIPv4(final String ip){
	      Pattern pattern = Pattern.compile(IPv4PATTERN);
	      Matcher matcher = pattern.matcher(ip);
	      return matcher.matches();             
	}
	
	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		ClientMainUI mainUI = new ClientMainUI();
//		mainUI.start();
//	}
	
	/**
	 * Create the frame.
	 */
	public ClientMainUI(final ClientV2Madapter v2madpter)
	{
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				logout();
				dispose();
			}
		});
		this.v2madpter = v2madpter;
		window = new ClientLogUI(this.v2madpter, this.textArea);
		setTitle("GASS - Group Anti-temper Share System");
		initGUI();
	}
	


	private void initGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1100, 520);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		contentPane.add(pnlController1, BorderLayout.NORTH);
		pnlController1.setLayout(new BorderLayout(0, 0));
		
		pnlController1.add(panel_2, BorderLayout.CENTER);
		btnDownload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				v2madpter.downloadSeverFile(folderList.getSelectedValue());
			}
		});
		
		panel_2.add(btnDownload);
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				v2madpter.refreshSeverFile();
			}
		});
		
		panel_2.add(btnRefresh);
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				v2madpter.deleteSeverFile(folderList.getSelectedValue());
				}
		});
		
		panel_2.add(btnDelete);
		btnUpload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog(contentPane);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					v2madpter.uploadLocalFile(file);
				//	v2madpter.uploadLocalFile(file.getAbsolutePath());
				}
			}
		});
		
		panel_2.add(btnUpload);
		
		contentPane.add(pnlDisplayInfo, BorderLayout.CENTER);
		GridBagLayout gbl_pnlDisplayInfo = new GridBagLayout();
		gbl_pnlDisplayInfo.columnWidths = new int[] {0, 0};
		gbl_pnlDisplayInfo.rowHeights = new int[] {0};
		gbl_pnlDisplayInfo.columnWeights = new double[]{0.4, 0.6};
		gbl_pnlDisplayInfo.rowWeights = new double[]{1.0};
		pnlDisplayInfo.setLayout(gbl_pnlDisplayInfo);
		
		GridBagConstraints gbc_folderList = new GridBagConstraints();
		gbc_folderList.insets = new Insets(0, 0, 0, 5);
		gbc_folderList.fill = GridBagConstraints.BOTH;
		gbc_folderList.gridx = 0;
		gbc_folderList.gridy = 0;
		pnlDisplayInfo.add(folderList, gbc_folderList);
		
		GridBagConstraints gbc_panel_3 = new GridBagConstraints();
		gbc_panel_3.fill = GridBagConstraints.BOTH;
		gbc_panel_3.gridx = 1;
		gbc_panel_3.gridy = 0;
		pnlDisplayInfo.add(panel_3, gbc_panel_3);
		panel_3.setLayout(new BorderLayout(0, 0));
		
		panel_3.add(panel_5, BorderLayout.SOUTH);
		btnMembership.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				memberUI.fresh();
				memberUI.setVisible(true);
			}
		});
		btnJoinGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ClientJoinDialog dialog = new ClientJoinDialog();
				dialog.setModal(true);
				dialog.setLocationRelativeTo(contentPane);
				dialog.setVisible(true);
				
				ArrayList<String> response = dialog.getResponse();
				if (response.size() > 0) {
					String ip = response.get(0);
					String code = response.get(1);
					if (validateIPv4(ip)) {
						System.out.println("ip: " + ip + ", code: " + code);
						v2madpter.sendJoinRequest(code, ip);
					}
					else
						textArea.append("The IP address you entered is not valid. (" + ip + ")");
				}
			}
		});
		
		panel_5.add(lblOperation);
		
		panel_5.add(btnJoinGroup);
		
		panel_5.add(btnMembership);
		btnLog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				window.start();
			}
		});
		panel_5.add(btnLog);
		btnLeaveGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int dialogButton =
                JOptionPane.showConfirmDialog (null,
                		"Do you really want to quit the group?",
                		"Warning", JOptionPane.YES_NO_OPTION);

                if (dialogButton == JOptionPane.YES_OPTION) {
                	System.out.println("Decide to quit the group!");
                	v2madpter.leave();
                }
			}
		});
		
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				v2madpter.logout();
				close();
				dispose();
			}
		});
		
		panel_5.add(btnClose);
		
		panel_5.add(lblToQuitThe);
		
		panel_5.add(btnLeaveGroup);
		textArea.setEditable(false);
		
		textArea.setBorder(BorderFactory.createTitledBorder("Information"));
		
		textArea.setCaretPosition(textArea.getDocument().getLength());
		
		panel_3.add(scrollPane, BorderLayout.CENTER);
		
		scrollPane.setViewportView(textArea);
		
		
		folderList.setBorder(BorderFactory.createTitledBorder("Files on server"));
	}
	
	public void start(){
		setVisible(true);
		folderList.setModel(new DefaultListModel<String>());
	}
	
	public void addFileOnList(String s) {
		DefaultListModel<String> listModel = (DefaultListModel<String>) folderList.getModel();
		listModel.addElement(s);
		folderList.setModel(listModel);
	}
	
	public void addFilesOnList(ArrayList<String> list) {
		DefaultListModel<String> listModel = (DefaultListModel<String>) folderList.getModel();
		for (String s : list)
			listModel.addElement(s);
		folderList.setModel(listModel);
	}

	public void deleteFileOnList(String s)
	{
		DefaultListModel<String> listModel = (DefaultListModel<String>) folderList.getModel();
		listModel.removeElement(s);
		folderList.setModel(listModel);
	}
	
	public void deleteAllFileOnList() {
		DefaultListModel<String> listModel = (DefaultListModel<String>) folderList.getModel();
		listModel.removeAllElements();
		folderList.setModel(listModel);
	}
	
	public void refreshList(ArrayList<String> list) {
		deleteAllFileOnList();
		addFilesOnList(list);
	}
	
	public void mergeLogFromServer() {
		window.mergeLog();
	}
	
	public void appendInfo(final String string) {
		// important to use the swing utility
		// because UI will freeze when other thread writes to the UI directly
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				textArea.append(string + "\n");
			}
		});
	}
	
	public void logout() {
		v2madpter.logout();
	}
	
	public void close() {
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}
}
