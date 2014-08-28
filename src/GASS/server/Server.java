package GASS.server;

import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.PrivateKey;
import java.security.PublicKey;

import stub.*;
import GASS.utils.CryptoKit;
import RMIPack.rmiUtils.*;
import RMIPack.util.IVoidLambda;

public class Server {
	/**
	 * registry
	 */
	private Registry registry;
	
	/**
	 * output command used to put multiple strings up onto the view.
	 */
	private IVoidLambda<String> outputCmd = new IVoidLambda<String>() {

		@Override
		public void apply(String... params) {
			for (String s : params) {
				System.out.println(s);
			}
		}
	};
	
	/* Identity and Key information */
	String identity = "GASS.Server";
	PublicKey publicKey;
	PrivateKey privateKey;
	
	boolean loadKey() {
		if (!CryptoKit.areKeysPresent(identity)) {
			if (!CryptoKit.generateKeyFile(identity))
				return false;
		}
		
		publicKey = CryptoKit.getPublicKey(identity);
		privateKey = CryptoKit.getPrivateKey(identity);
		
		return (publicKey != null && privateKey != null);
	}
	
	/* Start RMI */
	ServerHost host;
	IHost hostStub;
	String myIP;
	IRMIUtils rmiUtils = new RMIUtils(outputCmd);
	
	void start() {
		if (publicKey == null || privateKey == null) {
			System.err.println("Lack of RSA keys! Need both the public key and private key.");
			return;
		}
		
		myIP = rmiUtils.startRMI(IRMI_Defs.CLASS_SERVER_PORT, true);
		try {
			host = new ServerHost(identity, myIP, publicKey);
			System.out.println("Test information:\n"
					+ "identity: " + host.getIdentity() + "\n"
					+ "public key: " + host.getPublicKey());
			// Use this technique rather than the simpler "registry.rebind(name, engine);"
			// because it enables us to specify a port number so we can open that port on the firewall
			hostStub = (IHost) UnicastRemoteObject.exportObject(host, IHost.SERVER_CONNECTION_PORT);
			registry = rmiUtils.getLocalRegistry(IRMI_Defs.SERVER_REGISTRY_PORT);
			registry.rebind(IHost.HOST_SERVER_BOUND_NAME, hostStub);
			System.out.println("Host bound to " + IHost.HOST_SERVER_BOUND_NAME + ".");
		}
		catch (Exception e) {
			System.out.println("No connection established!\n");
			System.err.println("Host exception:"+"\n");
			e.printStackTrace();
			System.exit(-1);
		}

		System.out.println("Waiting..."+"\n");
	}
	
	public static void main(String[] args) {
		Server s = new Server();
		if (s.loadKey()) {
			s.start();
			while (true) {}
		}
		else
			System.err.println("Fail to load RSA key pairs.");
	}
}
