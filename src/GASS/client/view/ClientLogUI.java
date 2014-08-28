 package GASS.client.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.jdom2.JDOMException;

import test.SSHKit;
import GASS.utils.*;
import LocalLog.Log;
import LocalLog.XMLHelper;

import java.awt.event.ActionListener;

public class ClientLogUI {

	private JFrame frame;
	private JTable table;
	private JPanel contentPane;
	private JPanel panel_1;
	private JFrame frame_temp;
	private JButton enter_button;
	private JTextArea textField;
	DefaultTableModel tableModel;
	ClientV2Madapter v2madpter;
//	private ArrayList<Log> server_log = new ArrayList<Log>();
//	private ArrayList<Log> client_log = new ArrayList<Log>();
//	private ArrayList<Log> merge_log = new ArrayList<Log>();
	private String localLogPath= "." + File.separatorChar + "client_log.xml";
	private String newLogPath = "." + File.separatorChar + "download_server_log.xml";
	private String localLogRoot = "client-log";
	private String serverLogRoot = "server-log";
	JTextArea infoText;

	/**
	 * Launch the application.
	 */
/*	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientLogUI window = new ClientLogUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	*/

	public void start()
	{
		frame.setVisible(true);	
		showLocalLog();
	}

	/**
	 * Create the application.
	 */
	public ClientLogUI(ClientV2Madapter v2madpter, JTextArea infoText) {
		this.v2madpter = v2madpter;
		this.infoText = infoText;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		// Create_client_log_test client = new Create_client_log_test();
		
//		server_log.clear();
//		client_log.clear();
		frame = new JFrame();
		frame.setBounds(100, 100, 900, 600);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		
		frame_temp = new JFrame();
		frame_temp.setBounds(100, 100, 450, 300);
		panel_1 = new JPanel();
		panel_1.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel_1.setLayout(new BorderLayout(0, 0));
		frame_temp.getContentPane().add(panel_1);
		
		
		enter_button = new JButton("enter the name of log");
		enter_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
}
		});
		
//		enter_button.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent arg0) {
//				if(textField.getText() != null){
//					String log_name = textField.getText();
//					SSHKit.scpFrom(log_name, "server log");
//					}
//					String path = "C:\\Users\\Yinuo Wang\\Desktop";
//					try {
//						FileInputStream fis = new FileInputStream("server log");
//						FileOutputStream fos = new FileOutputStream(path);
//						appendInfo("Download completed.");
//						
//					} catch (Throwable e1) {
//						JOptionPane.showMessageDialog(contentPane, "File stream error occurs.");
//						e1.printStackTrace();
//						appendInfo("Download aborted.");
//					}
//					XMLHelper xml = new XMLHelper("C:\\Users\\Yinuo Wang\\Desktop", "server log");
//					Log log_test = new Log("Yinuo Wang", 2, "some file", "insertion", "04/01/2014");
//					try {
//						xml.addlog(log_test);
//					} catch (JDOMException | IOException e) {
//						e.printStackTrace();
//					}
//					tableModel = (DefaultTableModel) table.getModel();
//					ArrayList<Log> log_List = xml.getAllLog();
//					for(int i = 0; i < log_List.size(); i++){
//						Log Log_temp = log_List.get(i);
//						tableModel.addRow(new Object[]{Log_temp.getLogNo(), Log_temp.getidentity(), Log_temp.getopObject(), Log_temp.getSign(), Log_temp.getdate(), Log_temp.getOptype()});
//					}
//					
//			}
//		});
		panel_1.add(enter_button, BorderLayout.SOUTH);
		
		textField = new JTextArea();
		textField.setBackground(Color.LIGHT_GRAY);
		panel_1.add(textField, BorderLayout.CENTER);
		textField.setColumns(10);
		
		Object[][] cellData = null;
		String[] headers = {"logID","identity","opObject","sign","date", "Optype","Verify"};
		DefaultTableModel model = new DefaultTableModel(cellData, headers){
			public boolean isCellEditable(int row, int column){
				return false;
			}
			
		};
		table = new JTable(model);
		table.setFont(new Font("Euphemia", Font.ITALIC, 15));
		final DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
		int count=7;
		tableModel.setColumnCount(count);
		tableModel.addRow(new Object[]{"logID","identity","opObject","sign","date", "Optype","Verify"});
		frame.getContentPane().add(table, BorderLayout.CENTER);
		/**
		 * download log from server
		 * the new log from server name: download_server_log.xml
		 * the old log locally name: download_client_log.xml
		 */
		JButton btnNewButton = new JButton("download log from server");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			//	JOptionPane.showMessageDialog(panel_1, "input the name of the server log");
			//	frame_temp.setVisible(true);
			//	mergeLog();
				v2madpter.download_log();
				File baseDir = new File("." + File.separatorChar + "download_server_log.xml"); 
				if(baseDir != null) infoText.append("down load log success, baseDir is not null");
				// mergeLog();
			}
		});
		btnNewButton.setFont(new Font("Tw Cen MT Condensed Extra Bold", Font.ITALIC, 16));
		frame.getContentPane().add(btnNewButton, BorderLayout.NORTH);
	}
	public void insertionLog(Object[] objects){
		tableModel = (DefaultTableModel) table.getModel();
		tableModel.addRow(objects);
	}
	private void appendInfo(final String string) {
		textField.append(string + "\n");
	}
	/**
	 * show local log
	 */
	public void showLocalLog()
	{
		ArrayList<Log> client_log = new ArrayList<Log>();
		XMLHelper xml_client = new XMLHelper(localLogPath, localLogRoot);
		client_log = xml_client.getAllLog();
		tableModel = (DefaultTableModel) table.getModel();
		while (tableModel.getRowCount() > 1)
			tableModel.removeRow(1);
		for(Log cur : client_log )
		{
			PublicKey publickey=CryptoKit.getPublicKey(cur.getidentity());
		    if(CryptoKit.verifyWithRSA(cur.toString().getBytes(),cur.getSign(),publickey))
		    {
		    	tableModel.addRow(new Object[]{cur.getLogId(), cur.getidentity(),cur.getopObject(), 
					TransformKit.bytes2HexString(cur.getSign()), cur.getdate(), cur.getOptype(),"true"});
		    }
		   
		    else
		    	tableModel.addRow(new Object[]{cur.getLogId(), cur.getidentity(),cur.getopObject(), 
						TransformKit.bytes2HexString(cur.getSign()), cur.getdate(), cur.getOptype(),"false"});
		}
		table.setModel(tableModel);
	}
	/**
	 * merge local log and server log
	 */
	public void mergeLog()
	{
		ArrayList<Log> server_log = new ArrayList<Log>();
		ArrayList<Log> client_log = new ArrayList<Log>();
		ArrayList<Log> merge_log = new ArrayList<Log>();
		// TODO: get log from server using data packet
		//SSHKit.scpFrom("xml_test.xml", "server_log.xml");
		tableModel = (DefaultTableModel) table.getModel();
		XMLHelper xml = new XMLHelper(newLogPath, serverLogRoot);
		XMLHelper xml_client = new XMLHelper(localLogPath, localLogRoot);
		//store the informaiton from server log
		server_log = xml.getAllLog();
		client_log = xml_client.getAllLog();
		
		xml_client.init();
		while (tableModel.getRowCount() > 1)
			tableModel.removeRow(1);
		
		int i = 0;
		int j = 0;
		while(i < server_log.size() && j < client_log.size()){
			if(server_log.get(i).getLogId()==client_log.get(j).getLogId()){
				//if the log id are the same, compare the content of two log with the same id
				if(server_log.get(i).getdate() == client_log.get(j).getdate() && server_log.get(i).getidentity() == client_log.get(j).getidentity() && server_log.get(i).getopObject() == client_log.get(j).getopObject() && server_log.get(i).getOptype() == client_log.get(j).getOptype() && server_log.get(i).getSign() == client_log.get(j).getSign()){
					merge_log.add(server_log.get(i));
					tableModel.addRow(new Object[]{server_log.get(i).getLogId(),server_log.get(i).getidentity(),server_log.get(i).getopObject(), 
							TransformKit.bytes2HexString(server_log.get(i).getSign()), server_log.get(i).getdate(), server_log.get(i).getOptype(),"true"});
				}
				//if the content is not the same, append information
				else{
					tableModel.addRow(new Object[]{server_log.get(i).getLogId(),server_log.get(i).getidentity(),server_log.get(i).getopObject(), 
							TransformKit.bytes2HexString(server_log.get(i).getSign()), server_log.get(i).getdate(), server_log.get(i).getOptype(),"conflict log from server"});
					tableModel.addRow(new Object[]{client_log.get(j).getLogId(),client_log.get(j).getidentity(),client_log.get(j).getopObject(), 
							TransformKit.bytes2HexString(client_log.get(j).getSign()), client_log.get(j).getdate(), client_log.get(j).getOptype(),"conflict log from client"});
				}
				i++;
				j++;
			}
			else{//new log from server, need to be verify by the client
				Log temp = server_log.get(i);
				merge_log.add(temp);
				PublicKey publickey=CryptoKit.getPublicKey(temp.getidentity());
				if(CryptoKit.verifyWithRSA(temp.toString().getBytes(),temp.getSign(),publickey)){
					tableModel.addRow(new Object[]{server_log.get(i).getLogId(),server_log.get(i).getidentity(),server_log.get(i).getopObject(), 
							TransformKit.bytes2HexString(server_log.get(i).getSign()), server_log.get(i).getdate(), server_log.get(i).getOptype(),"true"});
				}
				else{
					tableModel.addRow(new Object[]{temp.getLogId(), temp.getidentity(),temp.getopObject(), 
							TransformKit.bytes2HexString(temp.getSign()), temp.getdate(), temp.getOptype(),"false"});
				}
				i++;
			}
		}
		while(i < server_log.size()){
			Log temp = server_log.get(i);
			PublicKey publickey=CryptoKit.getPublicKey(temp.getidentity());
			if(CryptoKit.verifyWithRSA(temp.toString().getBytes(),temp.getSign(),publickey)){
				tableModel.addRow(new Object[]{temp.getLogId(), temp.getidentity(),temp.getopObject(), 
						TransformKit.bytes2HexString(temp.getSign()), temp.getdate(), temp.getOptype(),"true"});
				merge_log.add(temp);
			}
			else{
				merge_log.add(temp);
				tableModel.addRow(new Object[]{temp.getLogId(), temp.getidentity(),temp.getopObject(), 
						TransformKit.bytes2HexString(temp.getSign()), temp.getdate(), temp.getOptype(),"false"});
			}
			i++;
		}
		client_log = merge_log;
		//write new client log
		for(Log temp: client_log){
			try {
				xml_client.addlog(temp);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}