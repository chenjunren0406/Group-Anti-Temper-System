package dataType;

import java.io.Serializable;
import java.security.PublicKey;

import GASS.utils.TransformKit;

public class LeaveRequest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3784833044404547187L;
	private String _identity;
	private PublicKey _publicKey;
	private byte[] _signedContent;
	private byte[] _sign;
	
	
	
	public LeaveRequest(String _identity, PublicKey _publicKey) {
		super();
		this._identity = _identity;
		this._publicKey = _publicKey;
		this._signedContent = TransformKit.byteMerger(_identity.getBytes(), _publicKey.getEncoded());
	}


	public String get_identity() {
		return _identity;
	}

	public byte[] get_signedContent() {
		return _signedContent;
	}

	public void set_sign(byte[] _sign) {
		this._sign = _sign;
	}


	public byte[] get_sign() {
		return _sign;
	}
	
}
