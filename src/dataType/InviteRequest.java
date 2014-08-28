package dataType;

import java.io.Serializable;
import java.security.PublicKey;

import GASS.utils.TransformKit;

public class InviteRequest implements Serializable{
	private static final long serialVersionUID = -2278178974637543766L;
	
	private String _accessCode;
	private String _identity;
	private String _ip;
	private PublicKey _publicKey;
	private byte[] _signedContent;
	private byte[] _sign;
	
	public InviteRequest(String accessCode, String identity, String ipAddr, PublicKey publicKey) {
		_accessCode = accessCode;
		_identity = identity;
		_ip = ipAddr;
		_publicKey = publicKey;
		_signedContent = TransformKit.byteMerger((_accessCode + _identity + _ip).getBytes(), _publicKey.getEncoded());
	}
	
	public byte[] getSignedContent() {
		return _signedContent;
	}
	
	public String getAccessCode() {
		return _accessCode;
	}
	
	public String getIdentity() {
		return _identity;
	}
	
	public String getIP() {
		return _ip;
	}
	
	public PublicKey getPublicKey() {
		return _publicKey;
	}
	
	public void setSignature(byte[] sign) {
		_sign = sign;
	}
	
	public byte[] getSignature() {
		return _sign;
	}
}
