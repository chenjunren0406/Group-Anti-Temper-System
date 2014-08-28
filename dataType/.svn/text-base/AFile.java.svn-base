package dataType;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class AFile implements Serializable {
	private static final long serialVersionUID = -3161186144427308406L;
	
	private String _name;
	private byte[] _file;
	private String _editInfo;
	private byte[] _sign;
	/**
	 * call the getP2pmessages
	 * if this file is already existed, then getP2Pmessage.size() == 0
	 * else, if the file is new, then getP2Pmessage.size() != 0
	 */
	private ArrayList<P2PMessage> p2p_message_list = new ArrayList<P2PMessage>();

	public AFile(String name) {
		_name = name;
	}
	
	public AFile (String name, byte[] file) {
		_name = name;
		_file = file;
	}
	
	public AFile(String name, byte[] file, String editionInfo) {
		_name = name;
		_file = file;
		_editInfo = editionInfo;
	}
	
	public AFile(String name, byte[] file, String editionInfo, byte[] signature) {
		_name = name;
		_file = file;
		_editInfo = editionInfo;
		_sign = signature;
	}
	
	public AFile(String name, byte[] file, String editionInfo, byte[] signature, P2PMessage p2p_message) {
		_name = name;
		_file = file;
		_editInfo = editionInfo;
		_sign = signature;
		this.p2p_message_list.add(p2p_message);
	}
	public AFile(String name, byte[] file, String editionInfo, byte[] signature, ArrayList<P2PMessage> p2p_message_list) {
		_name = name;
		_file = file;
		_editInfo = editionInfo;
		_sign = signature;
		this.p2p_message_list = p2p_message_list;
	}
	public void clear_p2p_message_list(){
		p2p_message_list.clear();
	}
	public void add_p2p_message(P2PMessage p2p_message){
		this.p2p_message_list.add(p2p_message);
	}
	public byte[] getFile() {
		return _file;
	}
	
	public String getName(){
		return _name;
	}
	
	public String getEditInfo() {
		return _editInfo;
	}
	
	public byte[] getSignature() {
		return _sign;
	}
	
	
	public ArrayList<P2PMessage> getp2pMessages(){
		return p2p_message_list;
	}
}
