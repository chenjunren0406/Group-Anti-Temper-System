package dataType;

import java.io.Serializable;

public class P2PMessage implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6244841150977839076L;
	
	
	private String from,to;
	private byte[] content;
	
	private MessageType type;
	
	/**
	 * switch (messagetype)
	 * case FeKey: title is the upload file name 
	 * case AddMember: title is the identity of new member
	 * case RemoveMember: title is the identity		content format is : (identity publicKey.encode())+(signature 128bits) 
	 * 
	 */
	private String title;
	
	public static enum MessageType{
		FeKey, AddMember, RemoveMember, Unknown
	}
	
	public P2PMessage(String from, String to, MessageType type, byte[] content, String title){
		this.from = from;
		this.to = to;
		this.type = type;
		this.content = content;
		this.title = title;
	}

	public String getFrom() {
		return from;
	}

	public String getTo() {
		return to;
	}

	public byte[] getContent() {
		return content;
	}

	public MessageType getType() {
		return type;
	}
    
	public String getTitle(){
		return title;
	}
	
}
