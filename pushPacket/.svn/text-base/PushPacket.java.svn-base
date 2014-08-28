package pushPacket;

import java.io.Serializable;


/**
 * used to push offline packet to client side
 *  
 * @author junrenchen
 *
 * @param <T>
 */
public class PushPacket<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7215666714636399532L;

	
	/**
	 * type of the content
	 */
	private Class<?> c;
	
	private T data;
	
	public PushPacket(Class<?> c,T data){
		this.c = c;
		this.data = data;
	}
	
	public T getData(){
		return data;
	}
	
	public Class<?> getType(){
		return c;
	} 
	
	
}
