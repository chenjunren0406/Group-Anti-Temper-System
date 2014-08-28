package dataType;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LoginInfo implements Serializable{
	private static final long serialVersionUID = 3880886743425323791L;
	
	String _id;
	String _ip;
	Date _date;
	byte[] _sign;
	
	public LoginInfo(String identity, String ipAddr, Date date) {
		_id = identity;
		_ip = ipAddr;
		_date = date;
	}
	
	public void setSignature(byte[] signature) {
		_sign = signature;
	}
	
	public String toString() {
		String dateStr = (new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH)).format(_date);
		return _id + "|" + _ip + "|" + dateStr;
	}
	
	public String getIdentity() {
		return _id;
	}
	
	public String getIPAddress() {
		return _ip;
	}
	
	public Date getDate() {
		return _date;
	}
	
	public byte[] getSignature() {
		return _sign;
	}
	
	/*
	public static void main(String[] args) {
		try {
			Date d1 = (new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH)).parse("2014-4-10");
			Date d2 = (new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH)).parse("2014-4-9");
			System.out.println(d1.compareTo(d2));	// out put: 1
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}*/
}
