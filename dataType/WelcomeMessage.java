package dataType;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;

import FileRecord.Filerec;

public class WelcomeMessage implements Serializable{
	private static final long serialVersionUID = 3512202286234274789L;
	private String sender;
	private String serverIP;
	private HashMap<String, PublicKey> pk;
	private ArrayList<Filerec> fr;
	private byte[] signContent;
	private byte[] sign;
	
	public WelcomeMessage(String sender, String currentServerIP,
			HashMap<String, PublicKey> publicKeyList, ArrayList<Filerec> fileRecordList) {
		this.sender = sender;
		serverIP = currentServerIP;
		pk = publicKeyList;
		fr = fileRecordList;
		signContent = null;
		sign = null;
	}
	
	public String getSender() {
		return sender;
	}
	
	public HashMap<String, PublicKey> getPublicKeys() {
		return pk;
	}
	
	public ArrayList<Filerec> getFileRecords() {
		return fr;
	}
	
	public byte[] getSignContent() {
		return signContent;
	}
	
	public byte[] getSignature() {
		return sign;
	}
	
	public String getServerIP() {
		return serverIP;
	}
	
	public void setSignContent(byte[] content) {
		signContent = content;
	}
	
	public void setSignature(byte[] sign) {
		this.sign = sign;
	}
}
