package GASS.utils;

public class TransformKit {
	
	public static String bytes2HexString(byte[] in) {
		StringBuffer sb = new StringBuffer();
		for (byte b : in) {
			sb.append(num2Hex((byte) ((b >> 4) & 0xF)));
			sb.append(num2Hex((byte) (b & 0xF)));
		}
		
		return new String(sb);
	}
	
	public static byte[] hexString2Bytes(String s) {
		if (s == null || s.length() % 2 != 0)
			return null;
		int len = s.length();
		byte[] ret = new byte[len/2];
		for (int i = 0; i < len; i += 2)
			ret[i/2] = (byte) (((hex2Num(s.charAt(i)) << 4) & 0xF0) | (hex2Num(s.charAt(i+1)) & 0xF));
		
		return ret;
	}
	
	private static char num2Hex(byte b) {
		if (b >= 0 && b <= 15) {
			if (b < 10)
				return (char) ('0' + b);
			else
				return (char) ('A' + b - 10);
		}
		else
			return '*';
	}
	
	private static byte hex2Num(char c) {
		if (c >= 'A' && c <= 'F')
			return (byte) (c - 'A' + 10);
		else if (c >= 'a' && c<= 'f')
			return (byte) (c - 'a' + 10);
		else
			return (byte) (c - '0');
	}
	
	   public static byte[] byteMerger(byte[] byte_1, byte[] byte_2){  
	        byte[] byte_3 = new byte[byte_1.length+byte_2.length];  
	        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);  
	        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);  
	        return byte_3;  
	    } 
}
