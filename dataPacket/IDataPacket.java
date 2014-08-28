package dataPacket;

import java.io.Serializable;

import stub.IHost;

public interface IDataPacket<T> extends Serializable{
	/**
	 * @return the type of the content
	 */
	public Class<?> getType();
	
	/**
	 * @return sender's stub
	 */
	// public IHost getSender();
	
	/**
	 * @return integer indicates the operation type
	 */
	public int getOp();
	
	/**
	 * @return content in this packet
	 */
	public T getContent();
}
