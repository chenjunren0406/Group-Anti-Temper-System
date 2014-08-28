package dataPacket;

import java.io.Serializable;

import LocalLog.Log;
import stub.IHost;

public class DataPacket<T> implements Serializable {
	
	private static final long serialVersionUID = 960229670121322280L;
	
	/**
	 * type of the content
	 */
	private Class<?> c;
	
	/**
	 * sender's stub
	 */
	// private IHost sender;
	
	/**
	 * IP address of sender
	 */
	private String senderIP;
	
	/**
	 * operation type
	 */
	private int operation;
	
	/**
	 * content of this packet
	 */
	private T data;
	
	/**
	 * corresponding log
	 */
	private Log log;
	
	public DataPacket(String ip, Class<?> c, int operation) {
		this.senderIP = ip;
		this.c = c;
		this.operation = operation;
		data = null;
		log = null;
	}
	
	/*
	public DataPacket(Class<?> c, int operation, IHost senderStub) {
		this.c = c;
		this.operation = operation;
		this.sender = senderStub;
	}*/
	
	public Class<?> getType() {
		return c;
	}
	
	/*
	public IHost getSender() {
		return sender;
	}*/
	
	public String getSenderIP() {
		return senderIP;
	}
	
	public int getOp() {
		return operation;
	}
	
	public T getContent() {
		return data;
	}
	
	public void setContent(T data) {
		this.data = data;
	}
	
	public Log getLog() {
		return log;
	}
	
	public void setLog(Log log) {
		this.log = log;
	}
}


