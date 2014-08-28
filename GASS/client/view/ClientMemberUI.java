package GASS.client.view;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;

import java.awt.GridLayout;
import java.io.File;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JScrollPane;
import javax.swing.JList;


public class ClientMemberUI extends JFrame{
	private static final long serialVersionUID = 6647369460386345126L;
	private final JPanel panel = new JPanel();
	private final JScrollPane scrollPane = new JScrollPane();
	private final JList<String> list = new JList<String>();
	
	/**
	 * Create the application.
	 */
	public ClientMemberUI() {
		list.setModel(new DefaultListModel<String>());
		initGUI();
		
	}
	private void initGUI() {
		setBounds(200, 200, 400, 300);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				setVisible(false);
			}
		});
		initialize();
		
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		scrollPane.setViewportView(list);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		panel.setLayout(new GridLayout(0, 1, 0, 0));
		showMember();
	}

	private void showMember() {
		// notify all other member
		File baseDir = new File("." + File.separatorChar); 
		String[] filelist = baseDir.list();
		DefaultListModel<String> listModel = (DefaultListModel<String>) list.getModel();
		listModel.clear();
		listModel.addElement("Current member:");
		listModel.addElement("");
		for (String s : filelist)
			if (s.endsWith(".publicKey"))
				listModel.addElement(s.substring(0, s.length()-10));
		list.setModel(listModel);
	}

	public void fresh() {
		showMember();
	}

}
