package stub;

import java.io.IOException;
import java.rmi.RemoteException;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.jdom2.JDOMException;

import GASS.client.model.ClientM2Vadapter;
import pushPacket.PushPacket;
import pushPacket.pushPacketExecutor;
import dataPacket.ClientDPExecutor;
import dataPacket.DataPacket;

public class ClientHost implements IHost{
	/**
	 * identity of the client
	 */
	private String id;
	
	/**
	 * public key of the client
	 */
	private PublicKey pk;
	
	/**
	 * private key of the client
	 */
	private PrivateKey privateKey;
	
	/**
	 * access code for verifying join request
	 */
	private String accessCode = "ACCESSCODE";
	
	/**
	 * executor to interpret received data packets
	 */
	private ClientDPExecutor dataPacketExecutor;
	
	/**
	 * stub to transfer data to server
	 */
	private IHost serverStub;
	
	/**
	 * current IP address of server
	 */
	private String serverIP;
	
	/**
	 * current IP address
	 */
	private String myIP;
	
	public ClientHost(String identity, String ip, PublicKey publicKey, PrivateKey privateKey, ClientM2Vadapter mainViewAdapter) {
		id = identity;
		myIP = ip;
		pk = publicKey;
		this.privateKey = privateKey;
		dataPacketExecutor = new ClientDPExecutor(mainViewAdapter);
		dataPacketExecutor.setAccessCode(accessCode);
	}
	
	public void setServerStub(IHost stub) {
		serverStub = stub;
	}
	
	public void setServerIP(String ip) {
		serverIP = ip;
	}

	@Override
	public String getIdentity() throws RemoteException {
		return id;
	}

	@Override
	public PublicKey getPublicKey() throws RemoteException {
		return pk;
	}
	
	@Override
	public void sendLocalHostStub(IHost localHostStub) throws RemoteException {
		// don't know what to do yet
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void transferData(DataPacket dp) throws RemoteException {
		dataPacketExecutor.execute(dp, new Object[] {serverStub, id, myIP, privateKey, serverIP});
	}

	@Override
	public void push(PushPacket pp) {
		try {
			pushPacketExecutor.execute(pp);
		} catch (JDOMException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

}
