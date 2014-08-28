package dataType;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;

public class AddMember implements Serializable {
	private static final long serialVersionUID = -682956072107650608L;
	
	private String identity;
	private PublicKey publicKey;
	/**
	 * approver's signature
	 */
	private byte[] sign;
	
	public AddMember(String newIdentity, PublicKey newPublicKey, byte[] signature) {
		identity = newIdentity;
		publicKey = newPublicKey;
		sign = signature;
	}
	
	public String getMemberIdentity() {
		return identity;
	}
	
	public PublicKey getMemberPublicKey() {
		return publicKey;
	}
	
	public byte[] getApproverSign() {
		return sign;
	}
}
