package FileRecord;

import java.io.Serializable;

public class Filerec implements Serializable{
	private static final long serialVersionUID = 2147428881620682894L;
	
	private String name;
	private String FEKey;

	public Filerec(String name,String FEKey){
		this.name = name;
		this.FEKey = FEKey;
	}
	
	public String getfilename(){
		return name;
	}
	
	public String getFEkey(){
		return FEKey;
	}
}
