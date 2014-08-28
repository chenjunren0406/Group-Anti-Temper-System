package LocalLog;

import java.io.Serializable;
import java.util.UUID;

public class Log implements Serializable{
	private static final long serialVersionUID = -7241430790047161640L;
	
	private UUID logId;
	private String identity;
	private int Optype;
	private String opObject;
	private byte[] sign;
	private String date;
	
	public Log(UUID uuid, String identity,int Optype, String opObject, String date) {
		this.logId = uuid;
		this.identity = identity;
		this.Optype = Optype;
		this.opObject = opObject;
		this.date = date;
	}
	
	public Log(String identity,int Optype, String opObject, String date){
		this.logId = UUID.randomUUID();
		this.identity = identity;
		this.Optype = Optype;
		this.opObject = opObject;
		this.date = date;
	}
	
	public UUID getLogId(){
		return logId;
	}
	
	public String getidentity(){
		return identity;
	}
	
	public String getopObject(){
		return opObject;
	}
	
	public byte[] getSign(){
		return sign;
	}
	
	public String getdate(){
		return date;
	}
	
	public int getOptype(){
		return Optype;
	}
	
	public String toString() {
		return logId.toString() + "|" + identity + "|" + Optype + "|" + opObject + "|" + date;
	}
	
	public void setSign(byte[] sign){
		this.sign = sign;
	}
}