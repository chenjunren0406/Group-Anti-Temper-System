package dataPacket;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import javax.swing.JOptionPane;

import org.jdom2.JDOMException;

import stub.IHost;
import dataType.AFile;
import dataType.AddMember;
import dataType.InviteRequest;
import dataType.P2PMessage;
import dataType.WelcomeMessage;
import FileRecord.FileManage;
import FileRecord.Filerec;
import GASS.client.model.ClientM2Vadapter;
import GASS.client.model.ClientModel;
import GASS.utils.CryptoKit;
import GASS.utils.FileKit;
import GASS.utils.TransformKit;
import LocalLog.Log;
import LocalLog.XMLHelper;
import RMIPack.rmiUtils.IRMIUtils;
import RMIPack.rmiUtils.IRMI_Defs;
import RMIPack.rmiUtils.RMIUtils;
import RMIPack.util.IVoidLambda;

/**
 * an executor to interpret received data packets on client
 * @author wyf
 *
 */
public class ClientDPExecutor {
	
	private ClientM2Vadapter mainViewAdpt;

	private final String filePath = "files" + File.separatorChar;
	
	/**
	 * to verify the new member has the permission code
	 */
	private String accessCode = null;
	
	/**
	 * path of operation log on client
	 */
	private final String operationLogPath = "client_log.xml";
	
	/**
	 * name of root element in operation log on client
	 */
	private final String operationLogRoot = "client-log";
	
	private String newLogPath = "." + File.separatorChar + "download_server_log.xml";
	
	/**
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
	
	IRMIUtils rmiUtils = new RMIUtils(outputCmd);
	
	public ClientDPExecutor(ClientM2Vadapter mainViewAdapter) {
		mainViewAdpt = mainViewAdapter;
	}
	
	public void setAccessCode(String code) {
		accessCode = code;
	}
	
	/**
	 * Get client stub using IP address in the cache hash table. 
	 * If it's a new one, lookup the stub using RMI and put it into cache
	 * @param ipAddr the client's IP address
	 * @return stub from the IP address
	 */
	private IHost getStub(String ipAddr) {
		try {
			Registry remoteRegistry = rmiUtils.getRemoteRegistry(ipAddr, IRMI_Defs.CLIENT_REGISTRY_PORT);
			IHost newStub = (IHost) remoteRegistry.lookup(IHost.HOST_CLIENT_BOUND_NAME);
			return newStub;
		} catch (Exception e) {
			System.err.println("Fail to get the stub from IP: " + ipAddr);
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * to interpret the received data packet
	 * @param dp data packet to be interpreted
	 * @param args arguments needed by the execution
	 * @return true if successful
	 */
	@SuppressWarnings("rawtypes")
	public boolean execute(DataPacket dp, Object[] args) {
		int op = dp.getOp();
		switch (op) {
		case OperationType.loginSuccessful:
			mainViewAdpt.appendInfo("");
			mainViewAdpt.appendInfo("log in server successfully! Server IP: " + dp.getSenderIP());
			break;
			
		case OperationType.loginFail:
			mainViewAdpt.appendInfo("");
			mainViewAdpt.appendInfo("log in server failed! Server IP: " + dp.getSenderIP());
			break;
			
		case OperationType.fileUpload:
			break;
			
		case OperationType.fileDownload:
			fileDownload(dp);
			break;
		
		case OperationType.logDownload:
			logDownload(dp);
			break;
			 
		case OperationType.fileDelete:
			break;
			
		case OperationType.fileList:
			fileListRefresh(dp);
			break;
			
		case OperationType.fileOpFail:
			break;
			
		case OperationType.keyRenew:
			break;
			
		case OperationType.keyRenewFail:
			break;
			
		case OperationType.inviteRequest:
			receiveInviteRequest(dp, (IHost)args[0], (String)args[1], (String)args[2], (PrivateKey)args[3], (String)args[4]);
			break;
			
		case OperationType.inviteApproved:
			addMember(dp);
			break;
			
		case OperationType.welcomeMsg:
			receiveWelcome(dp);
			break;
			
		case OperationType.inviteReject:
			break;
			
		case OperationType.inviteFail:
			break;
			
		case OperationType.leaveNotify:
			break;
			
		case OperationType.leaveACK:
			break;
			
		case OperationType.leaveFail:
			break;
			
		case OperationType.textMessage:
			mainViewAdpt.appendInfo("");
			mainViewAdpt.appendInfo("Receive text message: " + dp.getContent() + ", from: " + dp.getSenderIP());
			break;
			
		default:
			System.out.println("Encounter unknown operation type. Code: " + op);
			return false;
		}
		
		return true;
	}

	public void fileDownload(final DataPacket<AFile> dp) {
		new Thread(){
			public void run(){
				mainViewAdpt.appendInfo("");
				AFile file=(dp.getContent());
				String identity=(file.getEditInfo().split("\\|"))[1];
				mainViewAdpt.appendInfo("Download file ''"+file.getName()+"''");
				mainViewAdpt.appendInfo("Signed by ''"+identity+"''");
				PublicKey pk=CryptoKit.getPublicKey(identity);
				System.out.println(TransformKit.bytes2HexString(file.getSignature()));
				if (pk != null && CryptoKit.verifyWithRSA(
							TransformKit.byteMerger(file.getFile(), file.getEditInfo().getBytes()),
							file.getSignature(), pk)){
					mainViewAdpt.appendInfo("Verify successfully!");
					FileManage fm=new FileManage();
					Filerec r=fm.findFEKey(file.getName());
					if (r!=null){
						ByteArrayInputStream bais = new ByteArrayInputStream(file.getFile());
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						try {
							CryptoKit.decryptAES(TransformKit.hexString2Bytes(r.getFEkey()), bais, baos);
							FileKit.write(baos.toByteArray(), new File(filePath+file.getName()));
							mainViewAdpt.appendInfo("Download file successfully");
						} catch (Throwable e) {
							mainViewAdpt.appendInfo("Fail to decrypt file or write to disk.");
							e.printStackTrace();
						}
						
					}else{
						mainViewAdpt.appendInfo("Can not find corresponding file encrypted key!");
					}
				}else{
					mainViewAdpt.appendInfo("Verify failed!");
				}
			}
		}.start();
	}
 
	/**
	 * down load log from server
	 * store the log into local
	 * the new log name: download_server_log.xml
	 * @param dp
	 */
	public void logDownload(final DataPacket<AFile> dp) {
		new Thread(){
			public void run(){
				mainViewAdpt.appendInfo("");
				AFile file=(dp.getContent());
				ByteArrayInputStream bais = new ByteArrayInputStream(file.getFile());
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				try {
					doCopy(bais, baos);
					FileKit.write(baos.toByteArray(), new File(newLogPath));
				//	FileKit.write(baos.toByteArray(), new File(filePath+file.getName()));
					mainViewAdpt.appendInfo("Download server log successfully");
					mainViewAdpt.receiveLogFromServer();
				} catch (Throwable e) {
					mainViewAdpt.appendInfo("Fail to Download server log");
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	public void fileListRefresh(final DataPacket<ArrayList<String>> dp) {
		(new Thread() {
			public void run() {
				mainViewAdpt.appendInfo("");
				mainViewAdpt.refreshFileList(dp.getContent());
				mainViewAdpt.appendInfo("Refresh file list successfully!");
			}
		}).start();
	}
	
	public void receiveInviteRequest(final DataPacket<InviteRequest> dp, final IHost serverStub,
			final String myId, final String myIP, final PrivateKey myPrivateKey,
			final String serverIP) {
		(new Thread() {
			public void run() {
				mainViewAdpt.appendInfo("");
				System.out.println("receive a request...");
				InviteRequest request = dp.getContent();
				File baseDir = new File("." + File.separatorChar);
				String[] filelist = baseDir.list();
				// check duplicate
				for (String f : filelist)
					if (f.equals(request.getIdentity() + ".publicKey")) {
						mainViewAdpt.appendInfo("Receive a request with identity \"" + request.getIdentity()
								+ "\" which already exists.");
						DataPacket<String> failMessage = new DataPacket<String>(myIP, String.class, OperationType.inviteFail);
						failMessage.setContent("The identity already exists!");
						IHost senderStub = getStub(dp.getSenderIP());
						if (senderStub != null)
							try {
								senderStub.transferData(failMessage);
							} catch (RemoteException e) {
								
								e.printStackTrace();
							}
						return;
					}
				
				System.out.println("it's a new request!");
				String newID = request.getIdentity();
				String newIP = request.getIP();
				PublicKey newPK = request.getPublicKey();
				mainViewAdpt.appendInfo("Receive a request to join the group. From:\n\tIdentity: "
						+ newID + "\n\tIP: " + newIP);
				if (CryptoKit.verifyWithRSA(request.getSignedContent(), request.getSignature(), newPK)) {
					if (accessCode != null && accessCode.equals(request.getAccessCode())) {
						int dialogButton = 
		                JOptionPane.showConfirmDialog (null,
		                		"Receive a request to join the group:"
		                		+ "\n\tAccess Code: " + request.getAccessCode()
		                		+ "\n\tIdentity: " + newID
		                		+ "\n\tIP: " + newIP,
		                		"Warning", JOptionPane.YES_NO_OPTION);
	
		                if(dialogButton == JOptionPane.YES_OPTION){
		                	// approve the request
		                	mainViewAdpt.appendInfo("You approve the request!");
		                	// create file for new public key
		                	CryptoKit.writePublicKeyToFile(newID, newPK);
		                	
		                	// send an AddMember to server
		                	if (serverStub != null) {
			                	byte[] toSign = TransformKit.byteMerger(newID.getBytes(), newPK.getEncoded());
			                	AddMember newMemberInfo = new AddMember(newID, newPK,
			                			CryptoKit.signWithRSA(toSign, myPrivateKey));
								
								// log for data packet of AddMember
								Date d = new Date(System.currentTimeMillis());
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
								String date = sdf.format(d);
								Log current_log = new Log(myId, OperationType.inviteApproved, newID, date);
								String sign = current_log.toString();
								current_log.setSign(CryptoKit.signWithRSA(sign.getBytes(), myPrivateKey));
								
								System.out.println("signed content in log: " + sign);
								XMLHelper helper = new XMLHelper(operationLogPath, operationLogRoot);
								
								try {
									helper.addlog(current_log);
								} catch (Exception e1) {
									e1.printStackTrace();
								}
								
								// construct the packet
								DataPacket<AddMember> dpToSend = new DataPacket<AddMember>(
										myIP, AddMember.class, OperationType.inviteApproved);
					            dpToSend.setContent(newMemberInfo);
					            dpToSend.setLog(current_log);
					            
					            // send the packet to server
					            try {
									serverStub.transferData(dpToSend);
									mainViewAdpt.appendInfo("Already sent the add-member message to server.");
								} catch (RemoteException e) {
									e.printStackTrace();
									mainViewAdpt.appendInfo("Fail to send add-member message to server!");
									return;
								}
		                	}
				            
				            // send file records and publicKeys to the new member
				            HashMap<String, PublicKey> publicKeys = new HashMap<String, PublicKey>();
				            ArrayList<Filerec> fileRecords = new ArrayList<Filerec>();
				            ArrayList<String> idForPK = new ArrayList<String>();
				            System.out.println("Get public keys of members:");
				            for (String f : filelist) {
				            	if (!f.equals(newID + ".publicKey") && f.endsWith(".publicKey")) {
				            		String id = f.substring(0, f.length() - 10);
				            		System.out.print("\t" + id + " ");
				            		PublicKey key = CryptoKit.getPublicKey(id);
				            		System.out.println((key != null));
				            		idForPK.add(id);
				            		publicKeys.put(id, CryptoKit.getPublicKey(id));
				            	}
				            }
				            FileManage fm = new FileManage();
				            fileRecords.addAll(fm.getAllFile());
				            
				            // data packet of WelcomeMessage
				            WelcomeMessage welcome = new WelcomeMessage(myId, serverIP, publicKeys, fileRecords);
				            Random r = new Random(System.currentTimeMillis());
				            PublicKey pk1 = publicKeys.get(idForPK.get(r.nextInt(publicKeys.size())));
				            PublicKey pk2 = publicKeys.get(idForPK.get(r.nextInt(publicKeys.size())));
				            byte[] content = TransformKit.byteMerger(pk1.getEncoded(), pk2.getEncoded());
				            welcome.setSignContent(content);
				            welcome.setSignature(CryptoKit.signWithRSA(content, myPrivateKey));
				            
				            DataPacket<WelcomeMessage> dpToNewMember = new DataPacket<WelcomeMessage>(
				            		myIP, WelcomeMessage.class, OperationType.welcomeMsg);
				            dpToNewMember.setContent(welcome);
				            
				            try {
				            	IHost clientStub = getStub(newIP);
				            	clientStub.transferData(dpToNewMember);
				            	mainViewAdpt.appendInfo("Already sent welcome message to new member!");
				            }
				            catch (Exception e) {
				            	e.printStackTrace();
				            	mainViewAdpt.appendInfo("Fail to send welcome message to new member!");
				            	return;
				            }
		                }
		                else
		                	mainViewAdpt.appendInfo("You already deny the request.");
					}
					else
						mainViewAdpt.appendInfo("Access Code verification fails!");
				}
				else
					mainViewAdpt.appendInfo("Signature verification fails!");
			}
		}).start();
	}
	
	
	
	private void addMember(final DataPacket<AddMember> dp) {
		Log log = dp.getLog();
		System.out.println("add member: ");
		System.out.println("log.toString(): " + log.toString());
		System.out.println("signature: " + TransformKit.bytes2HexString(log.getSign()));
		mainViewAdpt.appendInfo("");
		PublicKey clientPK = CryptoKit.getPublicKey(log.getidentity());
		// verify the log information
		if (clientPK != null && CryptoKit.verifyWithRSA(
				log.toString().getBytes(), log.getSign(), clientPK)) {
			// add this operation into operation Log
			XMLHelper xhelper = new XMLHelper(operationLogPath, operationLogRoot);
			try {
				xhelper.addlog(log);
			} catch (Exception e) {
				e.printStackTrace();
			}

			// get new member
			AddMember info = dp.getContent();
			byte[] signFromApprover = info.getApproverSign();
			String newId = info.getMemberIdentity();
			PublicKey newPublicKey = info.getMemberPublicKey();
			String approverID = log.getidentity();
			PublicKey approverPK = CryptoKit.getPublicKey(approverID);
			if (approverPK != null && CryptoKit.verifyWithRSA(
					TransformKit.byteMerger(newId.getBytes(), newPublicKey.getEncoded()), 
					signFromApprover, approverPK)) {
				// create file for public key
				CryptoKit.writePublicKeyToFile(newId, newPublicKey);
				mainViewAdpt.appendInfo("A new member has been added:\n\t" + "Identity: " + newId
						+ ", approved by: " + approverID);
			}
			else
				System.out.println("AddMember signature verification fails! From: " + dp.getSenderIP()
						+ ", identity in log: " + log.getidentity());
		}
		else
			System.out.println("Log of AddMember signature verification fails! From: " + dp.getSenderIP()
					+ ", identity in log: " + log.getidentity());
	}
	
	private void receiveWelcome(DataPacket<WelcomeMessage> dp) {
		WelcomeMessage msg = dp.getContent();
		String senderID = msg.getSender();
		String serverIP = msg.getServerIP();
		HashMap<String, PublicKey> pks = msg.getPublicKeys();
		Set<String> idSet = pks.keySet();
		if (!idSet.contains(senderID)) {
			mainViewAdpt.appendInfo("The welcome message doesn't contain in the public key set");
			return;
		}
		else
			CryptoKit.writePublicKeyToFile(senderID, pks.get(senderID));
		
		if (CryptoKit.verifyWithRSA(msg.getSignContent(), msg.getSignature(), pks.get(senderID))) {
			for (String id : idSet)
				if (!id.equals(senderID))
					CryptoKit.writePublicKeyToFile(id, pks.get(id));
			FileManage fm = new FileManage();
			fm.init();
			
			for (Filerec fr : msg.getFileRecords())
				try {
					fm.addFileRecord(fr);
				} catch (Exception e) {
					e.printStackTrace();
				}
			mainViewAdpt.appendInfo("Already received file keys and all members' public keys.");
			mainViewAdpt.appendInfo("Current server's IP address is: " + serverIP);
			JOptionPane.showMessageDialog(null, "Server IP address:\n" + serverIP +
					"\nYou need to close the application and log in again.");
		}
		else
			mainViewAdpt.appendInfo("The welcome signature is not verified!");
	}
	
	private void doCopy(InputStream is, OutputStream os) throws IOException {
		byte[] bytes = new byte[64];
		int numBytes;
		while ((numBytes = is.read(bytes)) != -1) {
			os.write(bytes, 0, numBytes);
		}
		os.flush();
		os.close();
		is.close();
	}
}
