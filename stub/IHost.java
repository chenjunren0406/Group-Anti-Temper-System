package stub;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PrivateKey;
import java.security.PublicKey;

import pushPacket.PushPacket;
import dataPacket.DataPacket;

public interface IHost extends Remote {
	/**
	 * The name to which the IHost object is bound to in the Server's Registry.
	 */
	public static final String HOST_SERVER_BOUND_NAME = "GASS.ServerStub";
	
	/**
	 * The name to which the IHost object is bound to in the Client's Registry.
	 */
	public static final String HOST_CLIENT_BOUND_NAME = "GASS.ClientStub";
	
	/**
	 * Port that IHost stubs use.
	 */
	public static final int SERVER_CONNECTION_PORT = 2101;
	
	public static final int CLIENT_CONNECTION_PORT = 2102;
	
	/**
	 * @return The identity information of this host
	 * @throws RemoteException RMI exception
	 */
	public String getIdentity() throws RemoteException;
	
	/**
	 * @return String that contains public key of this host
	 * @throws RemoteException RMI exception
	 */
	public PublicKey getPublicKey() throws RemoteException;
	
	/**
	 * called to send your own stub to this host
	 * @param localHostStub the stub to be sent
	 * @throws RemoteException RMI exception
	 */
	public void sendLocalHostStub(IHost localHostStub) throws RemoteException;
	
	/**
	 * send a data packet to this host
	 * @param dp the data packet to be transfered
	 * @throws RemoteException RMI exception
	 * @throws IOException 
	 */
	@SuppressWarnings("rawtypes")
	public void transferData(DataPacket dp) throws RemoteException;
	
	public void push(PushPacket pp) throws RemoteException;
}
