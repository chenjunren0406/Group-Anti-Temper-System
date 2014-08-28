package GASS.client.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

import javax.security.auth.login.LoginContext;
import javax.swing.JOptionPane;

import org.jdom2.JDOMException;

import pushPacket.PushPacket;
import dataPacket.ClientDPExecutor;
import dataPacket.DataPacket;
import dataPacket.OperationType;
import dataType.AFile;
import dataType.InviteRequest;
import dataType.LeaveRequest;
import dataType.P2PMessage;
import dataType.LoginInfo;
import stub.*;
import FileRecord.FileManage;
import FileRecord.Filerec;
import GASS.utils.CryptoKit;
import GASS.utils.FileKit;
import GASS.utils.FileToSend;
import GASS.utils.TransformKit;
import LocalLog.Log;
import RMIPack.rmiUtils.*;
import RMIPack.util.IVoidLambda;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientModel {
	private static final String IPv4PATTERN = 
	        "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
	        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
	        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
	        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

	boolean validateIPv4(final String ip){
	      Pattern pattern = Pattern.compile(IPv4PATTERN);
	      Matcher matcher = pattern.matcher(ip);
	      return matcher.matches();             
	}
	
	/**
	 * registry
	 */
	private Registry registry;
	/**
	 * Model to view adapter
	 */
	public static ClientM2Vadapter viewAdapter;
	
	String identity = null;
	
	public ClientModel(ClientM2Vadapter view){
		viewAdapter = view;
		String myIdentity = JOptionPane.showInputDialog("Identity:");
		if (myIdentity != null)
			identity = myIdentity;
		else {
			view.appendInfo("");
			view.appendInfo("Unknown identity!");
			return;
		}
		if(!loadKey()) view.appendInfo("your key does not exist!"); ;
	}
	
	/**
	 * 
	 * output command used to put multiple strings up onto the view.
	 */
	private IVoidLambda<String> outputCmd = new IVoidLambda<String>() {

		@Override
		public void apply(String... params) {
			for (String s : params) {
				System.out.println(s);
			}
		}
	};
	
	/* Identity and Key information */
	PublicKey publicKey;
	static PrivateKey privateKey;

	boolean loadKey() {
		if (!CryptoKit.areKeysPresent(identity)) {
			if (!CryptoKit.generateKeyFile(identity))
				return false;
		}
		
		publicKey = CryptoKit.getPublicKey(identity);
		privateKey = CryptoKit.getPrivateKey(identity);
		
		return (publicKey != null && privateKey != null);
	}
	
	/* Start RMI */
	ClientHost host;
	IHost hostStub;
	String myIP;
	IRMIUtils rmiUtils = new RMIUtils(outputCmd);
	IHost serverHost = null;
	
	public void start() {
		if (publicKey == null || privateKey == null) {
			System.err.println("Lack of RSA keys! Need both the public key and private key.");
			return;
		}
		
		myIP = rmiUtils.startRMI(IRMI_Defs.CLASS_CLIENT_PORT, false);
		try{
			host = new ClientHost(identity, myIP, publicKey, privateKey, viewAdapter);
			System.out.println("Test information:\n"
					+ "identity: " + host.getIdentity() + "\n"
					+ "public key: " + host.getPublicKey());
			hostStub = (IHost) UnicastRemoteObject.exportObject(host, IHost.CLIENT_CONNECTION_PORT);
			registry = rmiUtils.getLocalRegistry(IRMI_Defs.CLIENT_REGISTRY_PORT);
			registry.rebind(IHost.HOST_CLIENT_BOUND_NAME, hostStub);
			System.out.println("Host bound to " + IHost.HOST_CLIENT_BOUND_NAME + ".");
			
			String inputServerIP = JOptionPane.showInputDialog("Server IP:");
			if (inputServerIP != null && validateIPv4(inputServerIP)) {
				Registry remoteRegistry = rmiUtils.getRemoteRegistry(inputServerIP, IRMI_Defs.SERVER_REGISTRY_PORT);
				serverHost = (IHost) remoteRegistry.lookup(IHost.HOST_SERVER_BOUND_NAME);
				if (serverHost != null) {
					host.setServerStub(serverHost);
					host.setServerIP(inputServerIP);
					login();
				}
			}
			else
				viewAdapter.appendInfo("You didn't input server IP or the input is invalid.\n");
//			System.out.println("Test information:\n"
//					+ "Server identity: " + serverHost.getIdentity() + 
//					"Server public key: " + serverHost.getPublicKey() + "\n");
			
			//System.out.println("send my stub to server...");
			//serverHost.sendLocalHostStub(hostStub);
			//System.out.println("finish sending stub...");
			
			//DataPacket<String> toSend = new DataPacket<String>(String.class, OperationType.textMessage);
			//toSend.setContent("Take over the world!");
//			DataPacket<String> toSend = new DataPacket<String>(myIP, String.class, OperationType.textMessage);
//			toSend.setContent("Take over the world!");
//			System.out.println("before sent...");
//			//serverHost.transferData(toSend);
//			System.out.println("after sent...");
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Finish echo on server!"+"\n");
	}
	
	
	/**
	 * Send login datapacket wrapping a LoginInfo to server
	 */
	private void login(){
		new Thread(){
			public void run(){
				DataPacket<LoginInfo> dp=new DataPacket<>(myIP, LoginInfo.class, OperationType.login);
				LoginInfo loginfo = new LoginInfo(identity, myIP, new Date());
				String sign = loginfo.toString();
				loginfo.setSignature(CryptoKit.signWithRSA(sign.getBytes(), privateKey));
				dp.setContent(loginfo);
				
				try {
					serverHost.transferData(dp);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}.start();  
	}
	
	/**
	 * Send exit datapacket wrapping a LoginInfo to server
	 */
	public void logout(){
		if (serverHost == null)
			return;
		DataPacket<LoginInfo> dp=new DataPacket<>(myIP, LoginInfo.class, OperationType.exit);
		LoginInfo loginfo = new LoginInfo(identity, myIP, new Date());
		String sign = loginfo.toString();
		loginfo.setSignature(CryptoKit.signWithRSA(sign.getBytes(), privateKey));
		dp.setContent(loginfo);
				
		try {
			serverHost.transferData(dp);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Download a file from server
	 * The function will open a new thread to do the download
	 * @param filename filename on server
	 * @return 
	 */
	public void download(final String filename){
		viewAdapter.appendInfo("");
		viewAdapter.appendInfo("Request to download file '" + filename + "' from server...");
		new Thread() {
			public void run(){
				try {
					DataPacket<String> dp = new DataPacket<String>(myIP, String.class, OperationType.fileDownload);
					//get system date
					Date d = new Date(System.currentTimeMillis());
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
					String date = sdf.format(d);
					// construct the log
					Log log = new Log(identity, OperationType.fileDownload, filename, date);
					log.setSign(CryptoKit.signWithRSA(log.toString().getBytes(), privateKey));
					dp.setContent(filename);
					dp.setLog(log);
					serverHost.transferData(dp);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	//down load log from server
	public void download_log(){
		new Thread(){
			public void run(){
				try {
					DataPacket<String> dp = new DataPacket<String>(myIP, String.class, OperationType.logDownload);
				//	dp.setContent(filename);
					//view.appendInfo("Request to download file '" + filename + "' from server...\n");
					serverHost.transferData(dp);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	public void upload(final File file){ 
		try {
			new Thread(){
				public void run(){
					viewAdapter.appendInfo("");
					ArrayList<P2PMessage> p2pMessageList = new ArrayList<P2PMessage>();
					int flag = 0;
					// encrypt the file
					FileToSend file_to_upload = null;
					FileManage file_manage = new FileManage();
					ArrayList<Filerec> file_records = file_manage.getAllFile();
					String key = null;
					for (Filerec fr : file_records){
						if (fr.getfilename().equals(file.getName())){
							key = fr.getFEkey();
							file_to_upload = FileKit.encryptFile(file, TransformKit.hexString2Bytes(key));
							flag = 1;
							break;
						}
					}
					
					if (key == null){
						flag = 0;
						//encrypt the file
						file_to_upload = FileKit.encryptFile(file, null);
						file_manage = new FileManage();
						file_records = file_manage.getAllFile();
						//get the FEkey for this file
						for (Filerec fr : file_records){
							if (fr.getfilename().equals(file.getName())){
								key = fr.getFEkey();
								break;
							}
						}
						//
						File baseDir = new File("." + File.separatorChar); 
						String[] filelist = baseDir.list(); 
						for(int i = 0; i < filelist.length; i++){
							File readfile = new File(filelist[i]);
							if(readfile.getName().indexOf(".publicKey") != -1){
								int index = readfile.getName().indexOf(".publicKey");
								String to = readfile.getName().substring(0, index);
								if(to.equals(identity)) continue;
								else if(to.equals("GASS.Server")) continue;
								System.out.println(to);
								PublicKey other_key = CryptoKit.getPublicKey(to);
						        byte[] sign = CryptoKit.signWithRSA(TransformKit.hexString2Bytes(key), privateKey);
						        byte[] protect = CryptoKit.RSAPublicProcess(TransformKit.hexString2Bytes(key), other_key);
								byte[] content = TransformKit.byteMerger(sign, protect);
								String name = file.getName();
						        P2PMessage p2p_message = new P2PMessage(identity, to, P2PMessage.MessageType.FeKey, content,name);
								p2pMessageList.add(p2p_message);
							}
						}
					}
	
					//get system date
					Date d = new Date(System.currentTimeMillis());
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
					String date = sdf.format(d);
					
					//log 
					Log current_log = new Log(identity, OperationType.fileUpload, file_to_upload.getName(), date);
					String sign = current_log.toString();
					current_log.setSign(CryptoKit.signWithRSA(sign.getBytes(), privateKey));
					
					//file_for_datapacket
					String filepath = file_to_upload.getPath();
					byte[] file_content = FileKit.readFileIntoByteArray(filepath);
					AFile file_for_datapacket;
					if(flag == 1){
						file_for_datapacket = new AFile(file_to_upload.getName(),file_content, current_log.toString(), 
								CryptoKit.signWithRSA(TransformKit.byteMerger(file_content, current_log.toString().getBytes()), privateKey));
					}
					else{
						file_for_datapacket = new AFile(file_to_upload.getName(),file_content, current_log.toString(), 
								CryptoKit.signWithRSA(TransformKit.byteMerger(file_content, current_log.toString().getBytes()), privateKey),p2pMessageList);
					    System.out.println("this is a new file!");
					}
					
		            //go for completed datapacket
		            DataPacket<AFile> dataPacket_upload = new DataPacket<AFile>(myIP, AFile.class, OperationType.fileUpload);
		            dataPacket_upload.setContent(file_for_datapacket);
		            dataPacket_upload.setLog(current_log);
		            
		            viewAdapter.appendInfo("Sending file to server... File name: " + file_to_upload.getName());
		            //transfer data
		            try {
						serverHost.transferData(dataPacket_upload);
					} catch (IOException e) {
						e.printStackTrace();
					}
		            
		            viewAdapter.appendInfo("Upload Transfer completed. File name: " + file_to_upload.getName());
		            refreshSeverFile();
				}
			}.start();
			
		} catch (Throwable e1) {
			JOptionPane.showMessageDialog(null, "File stream error occurs.");
			e1.printStackTrace();
			viewAdapter.appendInfo("Upload aborted.");
		}
		
	}
	
	public void refreshSeverFile(){
		new Thread(){
			public void run(){
				try {
					viewAdapter.appendInfo("");
					DataPacket<String> dp=new DataPacket<String>(myIP, String.class, OperationType.fileList);
					
					//get system date
					Date d = new Date(System.currentTimeMillis());
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
					String date = sdf.format(d);
					
					// construct the log
					Log log = new Log(identity, OperationType.fileList, "list", date);
					log.setSign(CryptoKit.signWithRSA(log.toString().getBytes(), privateKey));
					dp.setContent("");
					dp.setLog(log);
					
					viewAdapter.appendInfo("Request to get the file list from server...");
					serverHost.transferData(dp);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	public void deleteSeverFile(final String filename){
		new Thread(){
			public void run(){
				try {
					viewAdapter.appendInfo("");
					DataPacket<String> dp=new DataPacket<String>(myIP, String.class, OperationType.fileDelete);
					dp.setContent(filename);
					
					//get system date
					Date d = new Date(System.currentTimeMillis());
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
					String date = sdf.format(d);
					
					//log 
					Log current_log = new Log(identity, OperationType.fileDelete, filename, date);
					String sign = current_log.toString();
					current_log.setSign(CryptoKit.signWithRSA(sign.getBytes(), privateKey));
					
					dp.setLog(current_log);
					
					viewAdapter.appendInfo("Request to delete file from server...");
					serverHost.transferData(dp);
					refreshSeverFile();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	
	public static void execute(PushPacket pp) throws JDOMException, IOException{
		viewAdapter.appendInfo("");
		 String FEkey = null;
		 P2PMessage p2pMessage = (P2PMessage) pp.getData();
		 String from = p2pMessage.getFrom();
		 String to = p2pMessage.getTo();
		 Object type = p2pMessage.getType();
		 String name = p2pMessage.getTitle();
		 System.out.println("this message is from " + from + " to " + to + " with the name " + name);
		 viewAdapter.appendInfo("this message is from " + from + " to " + to + " with the name " + name);
		 byte[] content = p2pMessage.getContent();
		 
		 //if the operation tye is fekey
		 if(P2PMessage.MessageType.FeKey.equals((P2PMessage.MessageType) type)){
			
            //transfer them to byte
			 byte[] sign = new byte[128];
			 byte[] protect = new byte[content.length - 128];
			 for (int i = 0; i < 128; i++)
				 sign[i] = content[i];
			 for (int i = 128; i < content.length; i++)
				 protect[i-128] = content[i];

			 //use private key to get plain text
			 byte[] text = CryptoKit.RSAPrivateProcess(protect, privateKey);
			 PublicKey clientPK = CryptoKit.getPublicKey(from);
			 if (clientPK != null && CryptoKit.verifyWithRSA(text, sign, clientPK)) {
				 FEkey = TransformKit.bytes2HexString(text);
				 Filerec file_rec = new Filerec(name, FEkey);
				 FileManage file_manage = new FileManage();
				 file_manage.addFileRecord(file_rec);
			 
				 viewAdapter.appendInfo("Received file key(s) from '" + from + "'");
			 }
			 else
				 viewAdapter.appendInfo("Received file key(s) from '" + from + "', but signature is not valid!");
		 }else if (P2PMessage.MessageType.RemoveMember.equals((P2PMessage.MessageType) type)){
            //transfer them to byte
			 byte[] sign = new byte[128];
			 byte[] protect = new byte[content.length - 128];
			 for (int i = 0; i < 128; i++)
				 sign[i] = content[i];
			 for (int i = 128; i < content.length; i++)
				 protect[i-128] = content[i];
			 
			 //verified
			 byte[] text=TransformKit.byteMerger(from.getBytes(), CryptoKit.getPublicKey(from).getEncoded());
			 PublicKey clientPK = CryptoKit.getPublicKey(from);
			 if (clientPK != null && CryptoKit.verifyWithRSA(text, sign, clientPK)){
				 File f=new File("." + File.separatorChar+from+".publicKey");
				 f.delete();
				 viewAdapter.appendInfo("Member '" + from + "' has quitted this share group!\n"
						 + "His/Her public key has been deleted");
			 }
			 else
				 viewAdapter.appendInfo("Member '" + from + "' attempts to quit the group,\n"
						 + "but the signature verification fails!");
		 }
	}
	
	public void sendRequest(final String accessCode, final String ip) {
		(new Thread() {
			public void run() {
				viewAdapter.appendInfo("");
				System.out.println("getting registry...");
				Registry remoteRegistry = rmiUtils.getRemoteRegistry(ip, IRMI_Defs.CLIENT_REGISTRY_PORT);
				try {
					System.out.println("getting stub...");
					IHost clientHost = (IHost) remoteRegistry.lookup(IHost.HOST_CLIENT_BOUND_NAME);
					System.out.println("got stub...");
					DataPacket<InviteRequest> dp = new DataPacket<InviteRequest>(
							myIP, InviteRequest.class, OperationType.inviteRequest);
					InviteRequest req = new InviteRequest(accessCode, identity, myIP, publicKey);
					req.setSignature(CryptoKit.signWithRSA(req.getSignedContent(), privateKey));
					dp.setContent(req);
					System.out.println("Start to send my request...");
					clientHost.transferData(dp);
					System.out.println("Finish to send my request...");
					viewAdapter.appendInfo("Already send the join request to client: " + ip + "\n");
				} catch (Exception e) {
					viewAdapter.appendInfo("Fail to send the join request!\n");
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	/**
	 * executed when a member wants to leave 
	 */
	public void leaveGroup(){
		/*
		send server a leave message
		inform all other member of the leaving
		*/
		new Thread(){
			public void run(){
				try {
					viewAdapter.appendInfo("");
					//notify server with 
					DataPacket<LeaveRequest> dp = new DataPacket<LeaveRequest>(
							myIP, LeaveRequest.class, OperationType.leaveNotify);
					LeaveRequest request=new LeaveRequest(identity, publicKey);
					request.set_sign(CryptoKit.signWithRSA(request.get_signedContent(), privateKey));
					dp.setContent(request);
					
					// log in data packet
					Date d = new Date(System.currentTimeMillis());
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
					String date = sdf.format(d);
					Log log = new Log(identity, OperationType.leaveNotify, identity, date);
					dp.setLog(log);
					
					serverHost.transferData(dp);
					viewAdapter.appendInfo("Already send the leave request to server");
					
					// notify all other member
					File baseDir = new File("." + File.separatorChar); 
					String[] filelist = baseDir.list();
					
					ArrayList<P2PMessage> p2pMessageList = new ArrayList<P2PMessage>();
					for (String s : filelist){
						if (s.endsWith(".publicKey")){
							String to=s.substring(0, s.length()-10);
							if(to.equals(identity)) continue;
							else if(to.equals("GASS.Server")) continue;
							viewAdapter.appendInfo("Informing "+to);
							PublicKey other_key = CryptoKit.getPublicKey(to);
							byte[] content=TransformKit.byteMerger(identity.getBytes(), publicKey.getEncoded());
							byte[] signature=CryptoKit.signWithRSA(content, privateKey);
							byte[] p2pcontent=TransformKit.byteMerger(signature, content);
					        P2PMessage p2p_message = new P2PMessage(identity, to, P2PMessage.MessageType.RemoveMember, p2pcontent, identity);
							p2pMessageList.add(p2p_message);
						}
					}
					//wrap the p2pMessageList as a datapacket and send it to server
					DataPacket<ArrayList<P2PMessage>> dataPac=new DataPacket<ArrayList<P2PMessage>>(identity, ArrayList.class, OperationType.broadcastMessage);
					dataPac.setContent(p2pMessageList);
					serverHost.transferData(dataPac);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
		
		
	}
}

