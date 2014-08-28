package pushPacket;

import java.io.IOException;
import java.security.PrivateKey;
import java.util.ArrayList;

import org.jdom2.JDOMException;

import GASS.client.model.ClientModel;
import GASS.utils.CryptoKit;
import dataPacket.DataPacket;
import dataType.P2PMessage;

public class pushPacketExecutor {
	public static void execute(PushPacket pp) throws JDOMException, IOException{
		ClientModel.execute(pp);
	}
}
