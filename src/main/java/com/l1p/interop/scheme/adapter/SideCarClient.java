package com.l1p.interop.scheme.adapter;

import java.io.IOException;
import java.net.URI;
import javax.websocket.Session;
import org.glassfish.tyrus.client.ClientManager;


public class SideCarClient {
	
	Session session;
	

	public SideCarClient(String sideCarServerHost, String sideCarServerPort) throws Exception {
		ClientManager client = ClientManager.createClient();
		 Session session = client.connectToServer(SideCarClient.class, new URI("ws://"+sideCarServerHost+":"+sideCarServerPort));
	}
	
	public void logForensicEntry(String entry) throws IOException {
		session.getBasicRemote().sendText(entry);
	}
}
