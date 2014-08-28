package dataPacket;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import pushPacket.PushPacket;
import pushPacket.pushMessageLog;
import stub.IHost;
import dataType.AFile;
import dataType.AddMember;
import dataType.LeaveRequest;
import dataType.LoginInfo;
import dataType.P2PMessage;
import FileRecord.FileManage;
import FileRecord.Filerec;
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
 * an executor to interpret received data packets on server
 * @author wyf, junren
 *
 */
public class ServerDPExecutor {
	/**
	 * share directory path
	 */
	private final String filePath = "files" + File.separatorChar;
	
	/**
	 * a hash table to cache all clients' stubs
	 */
	// private Hashtable<String, IHost> clientHosts;
	
	/**
	 * list of files stored in the share directory
	 */
	private ArrayList<File> fileList;
	
	/**
	 *  To keep Record of client's loginInfo, who is online.
	 */
	private HashMap<String, LoginInfo> clientLoginInfo;
	
	/**
	 * server's IP address
	 */
	private String myIP;
	
	/**
	 * lock to protect the file list from concurrent access
	 */
	private Lock fileListLock;
	
	/**
	 * lock to protect the loginInfo hashmap from concurrent access
	 */
	private Lock loginInfoLock;
	
	/**
	 * this is a log to store message those who is offline, but need to push some message to them after them online
	 */
	private pushMessageLog pushLog;
	
	/**
	 * path of operation log on server
	 */
	private final String operationLogPath = "server_op_log.xml";
	
	/**
	 * name of root element in operation log on server
	 */
	private final String operationLogRoot = "server-log";
	
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
	
	/**
	 * Initialize the executor with server's IP address
	 * @param myIP server's IP address
	 */
	public void init(String myIP) {
		// this.clientHosts = new Hashtable<String, IHost>();
		this.fileList = new ArrayList<File>();
		this.myIP = myIP;
		
		this.fileListLock = new ReentrantLock();
		
		this.loginInfoLock = new ReentrantLock();
		
		this.clientLoginInfo = new HashMap<String, LoginInfo>();
		
		this.pushLog = new pushMessageLog();
		// fill the file list
		File[] allFiles = null;
		try {
			allFiles = (new File(filePath)).listFiles();
		}
		catch (Exception e) {
			System.err.println("Fail to get files in share disrectory!");
			e.printStackTrace();
			return;
		}
		
		fileListLock.lock();
		try {
			for (File f : allFiles)
				fileList.add(f);
		}
		finally {
			fileListLock.unlock();
		}
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
			// clientHosts.put(ipAddr, newStub);
			return newStub;
		} catch (Exception e) {
			System.err.println("Fail to get the stub from IP: " + ipAddr);
			e.printStackTrace();
			return null;
		}
	}
	
	private void fileUpload(final DataPacket<AFile> dp, Object[] args){
		/*
		 * Get some information out of data packet
		 */
		
		AFile recvAfile = dp.getContent();
		
		byte[] filecontent =  recvAfile.getFile();
		
		Log fileLog = dp.getLog();
		
		byte[] logToByte = recvAfile.getEditInfo().getBytes();
		
		PublicKey clientPK = CryptoKit.getPublicKey(fileLog.getidentity());
		
		/*
		 * verify success
		 */
		if(clientPK != null && CryptoKit.verifyWithRSA(TransformKit.byteMerger(filecontent, logToByte), 
									recvAfile.getSignature(), 
									clientPK)) {
			System.out.println("file recv, verify success");
			String fileName = recvAfile.getName();
			
			File createFile = new File(filePath + fileName);
			File createLog = new File(filePath + fileName + ".log");
			
			try {
				/*
				 * create real file
				 */
				createFile.createNewFile();
				
				/*
				 * create .log file
				 */
				createLog.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				/*
				 * write .log file
				 */
				
				/*
				 *	xxx.log format is like getEditInfo() | signature()
				 */
				String logContent = recvAfile.getEditInfo() + "|" + TransformKit.bytes2HexString(recvAfile.getSignature());
				FileKit.write(logContent.getBytes(), createLog);
				/*
				 * write real file 
				 */
				FileKit.write(filecontent, createFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			/*
			 *  check file's name are in the file list or not;
			 *  if not, then add it
			 *  if it is in, then do nothing
			 */
			if(!fileList.contains(createFile)){
				fileListLock.lock();
				try{
					fileList.add(createFile);
				}
				finally{
					fileListLock.unlock();
				}
			}
			
			/*
			 * add this operation into operation Log
			 */
			XMLHelper xhelper = new XMLHelper(operationLogPath, operationLogRoot);
			try {
				xhelper.addlog(dp.getLog());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			broadcastMessage(recvAfile.getp2pMessages(), args, dp.getSenderIP());
		}
		
		/*
		 * is verify is failed,then send a failed datapacket back
		 */
		else{
			System.out.println(fileLog.getidentity() + "(" + dp.getSenderIP() + ") tries to upload a file, "
					+ "but signature is not valid.");
			final DataPacket<String> returndp = new DataPacket<String>(myIP, String.class, OperationType.fileOpFail);
			returndp.setContent("[Error from server] Signature verification failed");
			final String clientIP = dp.getSenderIP();
			
			(new Thread() {
				public void run() {
					try {
						IHost senderStub = getStub(clientIP);
						if (senderStub != null)
							senderStub.transferData(returndp);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}

	private void fileDownload(final DataPacket<String> dp, Object[] args){
		Log log = dp.getLog();
		PublicKey clientPK = CryptoKit.getPublicKey(log.getidentity());
		
		if (clientPK == null || !CryptoKit.verifyWithRSA(log.toString().getBytes(), log.getSign(), clientPK)) {
			System.out.println(log.getidentity() + " tries to download a file, but the signature is not valid.");
			return;
		}
		
		String requestFileName = dp.getContent();
		/*
		 *  To see file exist or not
		 */
		boolean fileExist = false;
		File requestFileDescriptor = null;
		
		fileListLock.lock();
		try{				
			for(File f : fileList){
				if(f.getName().equals(requestFileName)){
					requestFileDescriptor = f;
					fileExist = true;
					break;
				}
			}
		}
		finally{
			fileListLock.unlock();
		}
		
		/*
		 * file exist, then send it back to client
		 */
		if(fileExist == true){
			
			byte[] requestFileContent = FileKit.readFileIntoByteArray(requestFileDescriptor.getAbsolutePath());
			
			String logInfo = FileKit.readFileIntoString(filePath + requestFileName + ".log");
			System.out.println(logInfo);
			/*
			 *  To Get signature and editinfo
			 *  if failed, then two of them will be null !!!!!!
			 */
			int detectShuXian = 0;
			
			for(int i = logInfo.length() - 1 ; i >= 0; i--){
				if(logInfo.charAt(i) == '|'){
					detectShuXian = i;
					break;
				}
			}
			
			String editinfo = null;
			String sign = null;
			
			if(detectShuXian != 0){
				editinfo = logInfo.substring(0, detectShuXian);
				sign = logInfo.substring(detectShuXian + 1);	// exclude the ShuXian -.-
			}
			System.out.println(editinfo);
			System.out.println(sign);
			AFile fileToSend = new AFile(requestFileName,requestFileContent, editinfo, 
					TransformKit.hexString2Bytes(sign));
			
			final DataPacket<AFile> returndp = new DataPacket<AFile>(myIP, AFile.class, OperationType.fileDownload);
			System.out.println("Send file '" + requestFileName + "' to client: " + dp.getSenderIP());
			returndp.setContent(fileToSend);
			final String clientIP = dp.getSenderIP();
			
			(new Thread() {
				public void run() {
					try {
						IHost senderStub = getStub(clientIP);
						if (senderStub != null)
							senderStub.transferData(returndp);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
		/*
		 * file not exist, then send a failop datapacket
		 */
		else{
			final DataPacket<String> returndp = new DataPacket<String>(myIP,String.class, OperationType.fileOpFail);
			returndp.setContent("file not exist in server");
			final String clientIP = dp.getSenderIP();
			
			(new Thread() {
				public void run() {
					try {
						IHost senderStub = getStub(clientIP);
						if (senderStub != null)
							senderStub.transferData(returndp);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();

		}
	}
	
	/**
	 * download log information
	 * @param dp
	 * @param args
	 */
	private void logDownload(final DataPacket dp, Object[] args){
		String requestFileName = "server_op_log.xml";
		File requestFileDescriptor = null;
		File baseDir = new File("." + File.separatorChar); 
		String[] filelist = baseDir.list(); 
		for(int i = 0; i < filelist.length; i++){
			File readfile = new File(filelist[i]);
			if(readfile.getName().equals(requestFileName)){
				requestFileDescriptor = readfile;
				System.out.println("already found server_op_log.xml");
				break;
			}
		}
			
			byte[] requestFileContent = FileKit.readFileIntoByteArray(requestFileDescriptor.getAbsolutePath());
			System.out.println("requestFileContent has already been wraped");
			//down load log does not need log info and edit info
			//so editinfo and sign info are null
			String editinfo = null;
			String sign = null;
			
			AFile fileToSend = new AFile(requestFileName,requestFileContent, editinfo, 
					TransformKit.hexString2Bytes(sign));
			System.out.println("has filetosend to send");
			final DataPacket<AFile> returndp = new DataPacket<AFile>(myIP, AFile.class, OperationType.logDownload);
			System.out.println("Send file '" + requestFileName + "' to client: " + dp.getSenderIP());
			returndp.setContent(fileToSend);
			final String clientIP = dp.getSenderIP();
			
			(new Thread() {
				public void run() {
					try {
						IHost senderStub = getStub(clientIP);
						if (senderStub != null)
							senderStub.transferData(returndp);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
	}
	
	private void fileList(final DataPacket dp, Object[] args){
		Log log = dp.getLog();
		PublicKey clientPK = CryptoKit.getPublicKey(log.getidentity());
		
		if (clientPK == null || !CryptoKit.verifyWithRSA(log.toString().getBytes(), log.getSign(), clientPK)) {
			System.out.println(log.getidentity() + " tries to get the file list, but the signature is not valid.");
			return;
		}
		
		ArrayList<String> names = new ArrayList<String>();
		// get all files in the list
		fileListLock.lock();
		try {
			for (File f : fileList) {
				String n = f.getName();
				if (!n.endsWith(".log"))	// don't add file that is just a corresponding log
					names.add(f.getName());
			}
		}
		finally {
			fileListLock.unlock();
		}
		
		// send the list
		final DataPacket<ArrayList<String>> toSend = new DataPacket<ArrayList<String>>(
				myIP, ArrayList.class, OperationType.fileList);
		toSend.setContent(names);
		
		final String clientIP = dp.getSenderIP();
		
		(new Thread() {
			public void run() {
				try {
					IHost senderStub = getStub(clientIP);
					if (senderStub != null)
						senderStub.transferData(toSend);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	private void fileDelete(final DataPacket dp, Object[] args){
		Log recvLog = dp.getLog();
		PublicKey clientPK = CryptoKit.getPublicKey(recvLog.getidentity());
		if (clientPK != null && CryptoKit.verifyWithRSA(
				recvLog.toString().getBytes(), recvLog.getSign(), clientPK)) {
			// signature verified
			// update file list
			fileListLock.lock();
			try {
				File toRemove = null;
				for (File f : fileList) {
					if (f.getName().equals((String)dp.getContent())) {
						toRemove = f;
						f.delete();
						(new File(filePath + f.getName() + ".log")).delete();
						break;
					}
				}
				if (toRemove != null)
					fileList.remove(toRemove);
			}
			finally {
				fileListLock.unlock();
			}
			
			// write the deletion to log
			XMLHelper helper = new XMLHelper(operationLogPath, operationLogRoot);
			try {
				helper.addlog(recvLog);
			}
			catch (Exception e) {
				System.err.println("Fail to write the operation into server's log:\n\t"
						+ recvLog.toString() + "\n");
				e.printStackTrace();
			}
		}
	}

	private void broadcastMessage(final ArrayList<P2PMessage> need2Send, Object[] args, final String clientIp){
		if (need2Send == null || need2Send.size() == 0)
			return;
		//TODO delete after test
		System.out.println("BroadCast begin");
		
		String peopleOnline = "";
		String peopleOffline = "";
		
		for(P2PMessage p : need2Send){
			if(p.getTo().equals("GASS.Server")) {
				continue;
			}
			
			loginInfoLock.lock();
			try{
				/*
				 * this gay is online, then send to him
				 */
				if(clientLoginInfo.containsKey(p.getTo())){
					final PushPacket<P2PMessage> push = new PushPacket<P2PMessage>(P2PMessage.class, p);
					
					final String ipaddre = clientLoginInfo.get(p.getTo()).getIPAddress();
					
					//TODO delete after test
					System.out.println("BroadCast send from " + p.getFrom() +" to "+ p.getTo());
					(new Thread() {
						public void run() {
							try {
								getStub(ipaddre).push(push);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}).start();
					
					peopleOnline += p.getTo() + " ";
				}
				
				/*
				 * this gay is offline, then save it
				 */
				else{
					
					//TODO delete after test
					System.out.println("BroadCast save"+ " from " + p.getFrom() +" to "+ p.getTo());
					pushLog.addPushMessage(p.getFrom(), p.getTo(), p.getContent(), p.getType(),p.getTitle());
					peopleOffline += p.getTo() + " ";
				}
			}finally{
				loginInfoLock.unlock();
			}
			
		}
	}
	
	private void login(final DataPacket dp, Object[] args){
		
		//TODO delete after testing
		System.out.println("Someone Log into");
		
		
		LoginInfo info = (LoginInfo) dp.getContent();
		
		byte[] sign = info.getSignature();
		
		byte[] loginInfo2ByteArray = info.toString().getBytes();
		PublicKey clientPK = CryptoKit.getPublicKey(info.getIdentity());
		/*
		 * verify loginInfo valid or not
		 * 
		 * if it is valid
		 */
		if(clientPK != null && CryptoKit.verifyWithRSA(loginInfo2ByteArray, sign, clientPK)){
			
			//TODO delete after testing
			System.out.println("verify:  " +info.getIdentity() + " Log in");
			/*
			 * store it into client Login Info
			 */
			loginInfoLock.lock();
			try {
				clientLoginInfo.put(info.getIdentity(),info);
			}
			finally {
				loginInfoLock.unlock();
			}
			
			/*
			 * check pushlog, is there any information for this gay
			 */
			
			ArrayList<PushPacket<P2PMessage>> pushPackage = pushLog.getPushPacket(info.getIdentity());
			
			/*
			 * send him push message
			 */
			
			final IHost receiverStub = getStub(info.getIPAddress());
			System.out.println("IP: " + info.getIPAddress());
			System.out.println("get stub? " + (receiverStub != null) + ", size: " + pushPackage.size());
			if(receiverStub != null && pushPackage.size() > 0){
				//TODO  delete after test
				System.out.println("Someone comes online, begin to send message");
				
				for(final PushPacket<P2PMessage> p : pushPackage) {
					if (p.getData().getType().equals(P2PMessage.MessageType.FeKey)) {
						// FE key pushes
						(new Thread() {
							public void run() {
								try {
									receiverStub.push(p);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}).start();
					}
					else if (p.getData().getType().equals(P2PMessage.MessageType.AddMember)) {
						// new member pushes
						String[] title = p.getData().getTitle().split("\\|");
						String newMemberIdentity = title[0];
						String logId = title[1];
						byte[] signFromApprover = p.getData().getContent();
						String senderIP = p.getData().getFrom();
						final DataPacket<AddMember> toSend = new DataPacket<AddMember>(senderIP, AddMember.class, OperationType.inviteApproved);
						toSend.setContent(new AddMember(newMemberIdentity, CryptoKit.getPublicKey(newMemberIdentity), signFromApprover));
						XMLHelper helper = new XMLHelper(operationLogPath, operationLogRoot);
						Log logFromApprover = null;
						try {
							logFromApprover = helper.getOneLog(logId);
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						
						if (logFromApprover != null) {
							toSend.setLog(logFromApprover);
							(new Thread() {
								public void run() {
									try {
										receiverStub.transferData(toSend);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}).start();
						}
					}
				}
			}
			
			
			/*
			 * return dp to client 
			 */
			final DataPacket <String> returndp = new DataPacket<String>(myIP, String.class, OperationType.loginSuccessful);
			String successfulInfo = "login verify successful";
			returndp.setContent(successfulInfo);
			final String clientIP = dp.getSenderIP();
			
			(new Thread() {
				public void run() {
					try {
						IHost senderStub = getStub(clientIP);
						if (senderStub != null)
							senderStub.transferData(returndp);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
			
		}
			
		/*
		 * if it is not valid
		 */
		
		else{
			
			//TODO delete after testing
			System.out.println("verify failed:  " +info.getIdentity() + " attempts to Log in");
			
			final DataPacket<String> returndp = new DataPacket<String>(myIP, String.class, OperationType.loginFail);
			
			String errorInfo = "login verify failed";
			
			returndp.setContent(errorInfo);
			
			final String clientIP = dp.getSenderIP();
			
			(new Thread() {
				public void run() {
					try {
						IHost senderStub = getStub(clientIP);
						if (senderStub != null)
							senderStub.transferData(returndp);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
			
		}
	}
	
	private void logout(final DataPacket dp, Object[] args){
		System.out.println("Someone Log out");
		
		LoginInfo info = (LoginInfo) dp.getContent();
		
		byte[] sign = info.getSignature();
		
		byte[] loginInfo2ByteArray = info.toString().getBytes();
		
		PublicKey clientPK = CryptoKit.getPublicKey(info.getIdentity());
		
		/*
		 * verify loginInfo valid or not
		 * 
		 * if it is valid
		 */
		if(clientPK != null && CryptoKit.verifyWithRSA(loginInfo2ByteArray, sign, clientPK)){
			System.out.println("verify:  " +info.getIdentity() + " Log out");
			/*
			 * remove it from client Login Info
			 */
			loginInfoLock.lock();
			try {
				clientLoginInfo.remove(info.getIdentity());
			}
			finally {
				loginInfoLock.unlock();
			}
		}
		else
			System.out.println("verify failed:  " +info.getIdentity() + " attempts to log out.");
	}
	
	private void addMember(final DataPacket<AddMember> dp, Object[] args) {
		Log log = dp.getLog();
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
			if (approverPK != null &&
					CryptoKit.verifyWithRSA(TransformKit.byteMerger(newId.getBytes(), newPublicKey.getEncoded()), 
							signFromApprover, approverPK)) {
				// create file for public key
				System.out.println("write public key to local location...");
				CryptoKit.writePublicKeyToFile(newId, newPublicKey);
				// broadcast AddMember
				BroadcastAddMember(dp);
			}
			else
				System.out.println("AddMember signature verification fails! From: " + dp.getSenderIP()
						+ ", identity in log: " + log.getidentity());
		}
		else
			System.out.println("Log of AddMember signature verification fails! From: " + dp.getSenderIP()
					+ ", identity in log: " + log.getidentity());
	}
	
	private ArrayList<String> getAllMembers() {
		File baseDir = new File("." + File.separatorChar);
		String[] fileList = baseDir.list();
		ArrayList<String> allMembers = new ArrayList<String>();
		for (String file : fileList)
			if (!file.equals("GASS.Server.publicKey") && file.endsWith(".publicKey"))
				allMembers.add(file.substring(0, file.indexOf(".publicKey")));
		return allMembers;
	}
	
	private void BroadcastAddMember(final DataPacket<AddMember> dp) {
		//TODO delete after test
		System.out.println("BroadCast AddMember begin");
		String peopleOnline = "";
		String peopleOffline = "";
		String senderIdentity = dp.getLog().getidentity();
		AddMember newMember = dp.getContent();
		ArrayList<String> members = getAllMembers();
		
		loginInfoLock.lock();
		try {
			for (String memberName : members) {
				if (!memberName.equals(senderIdentity) && !memberName.equals(newMember.getMemberIdentity())) {
					if(clientLoginInfo.containsKey(memberName)) {
						// online, then send to him
						final String receiverIP = clientLoginInfo.get(memberName).getIPAddress();
						(new Thread() {
							public void run() {
								try {
									IHost clientStub = getStub(receiverIP);
									if (clientStub != null)
										clientStub.transferData(dp);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}).start();
						
						peopleOnline += memberName + " ";
					}
					else{
						// offline, then save it
						pushLog.addPushMessage(senderIdentity, memberName, newMember.getApproverSign(),
								P2PMessage.MessageType.AddMember,
								newMember.getMemberIdentity() + "|" + dp.getLog().getLogId());
						peopleOffline += memberName + " ";
					}
				}
			}
		}finally{
			loginInfoLock.unlock();
		}
		
		// TODO: delete after test
		System.out.println("people recv: " + peopleOnline + " people offline: " + peopleOffline);
		
		// return sender a dp packet
		final DataPacket<String> returnDp = new DataPacket<String>(myIP, String.class, OperationType.textMessage);
		final String senderIP = dp.getSenderIP();
		returnDp.setContent("people recv: " + peopleOnline + " people offline: " + peopleOffline);
		(new Thread() {
			public void run() {
				try {
					IHost senderStub = getStub(senderIP);
					if (senderStub != null)
						senderStub.transferData(returnDp);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	private void someOneLeave(DataPacket<LeaveRequest> dp) {
		LeaveRequest request = dp.getContent();
		String id = request.get_identity();
		System.out.println("'" + id + "' tries to leave the group.");
		byte[] content = request.get_signedContent();
		byte[] sign = request.get_sign();
		PublicKey clientPK = CryptoKit.getPublicKey(id);
		if (clientPK != null && CryptoKit.verifyWithRSA(content, sign, clientPK)) {
			System.out.println("Signature varified.");
			File f=new File("." + File.separatorChar + id + ".publicKey");
			f.delete();
			System.out.println("Member '" + id + "' has quitted this share group! "
					 + "His/Her public key has been deleted");
		}
		else
			System.out.println("Signature verification fails!");
	}
	
	/**
	 * static method to interpret the received data packet
	 * @param dp data packet to be interpreted
	 * @param args arguments needed by the execution
	 * @return true if successful
	 */
	@SuppressWarnings({ "rawtypes" })
	public boolean execute(final DataPacket dp, Object[] args) {
		int op = dp.getOp();
		switch (op) {
		case OperationType.fileUpload:
			
			fileUpload(dp, args);
			
			break;
			
		case OperationType.fileDownload:
			
			fileDownload(dp, args);
			
			break;
			
		case OperationType.fileList:
			
			fileList(dp, args);
			
			break;
			
		case OperationType.fileDelete:
			
			fileDelete(dp, args);
			
			break;
		
		/*
		 * when recv a broadcastMessage, send to someone who is in the list
		 * if he is in the list, then send it to him
		 * if he is not in the list, then keep it in a file and push it to this client when this gay log in
		 */
		case OperationType.broadcastMessage:
			ArrayList<P2PMessage> p2pMsg = (ArrayList<P2PMessage>)dp.getContent();
			broadcastMessage(p2pMsg, args, dp.getSenderIP());
			break;
			
		case OperationType.logDownload:
			logDownload(dp, args);
			break;
			
		case OperationType.fileOpFail:
			break;
			
		case OperationType.keyRenew:
			break;
			
		case OperationType.keyRenewFail:
			break;
			
		case OperationType.inviteApproved:
			addMember(dp, args);
			break;
			
		case OperationType.inviteReject:
			break;
			
		case OperationType.inviteFail:
			break;
			
		case OperationType.leaveNotify:
			someOneLeave(dp);
			break;
			
		case OperationType.leaveACK:
			break;
			
		case OperationType.leaveFail:
			break;
		
		case OperationType.textMessage:
			System.out.println("Receive text message from client:\n"
					+ (String)dp.getContent());
			break;
			
		case OperationType.login:
			login(dp, args);
			break;
			
		case OperationType.exit:
			logout(dp, args);
			break;
			
		default:
			System.out.println("Encounter unknown operation type. Code: " + op);
			return false;
		}
		
		return true;
	}
	
}
