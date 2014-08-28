package stub;

import java.rmi.RemoteException;
import java.security.PrivateKey;
import java.security.PublicKey;

import pushPacket.PushPacket;
import dataPacket.DataPacket;
import dataPacket.ServerDPExecutor;

public class ServerHost implements IHost{
	private String id;
	private PublicKey pk;
	private ServerDPExecutor executor;
	
	public ServerHost(String identity, String myIP, PublicKey publicKey) {
		id = identity;
		pk = publicKey;
		executor = new ServerDPExecutor();
		executor.init(myIP);
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
		// executor.addStub(null, localHostStub);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void transferData(DataPacket dp) throws RemoteException {
		executor.execute(dp, null);
	}

	/**
	 * do nothing 
	 */
	@Override
	public void push(PushPacket pp) throws RemoteException {
		// do nothing
	}




}
