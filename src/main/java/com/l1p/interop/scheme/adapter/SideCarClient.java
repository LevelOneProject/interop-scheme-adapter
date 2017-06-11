package com.l1p.interop.scheme.adapter;

import java.io.IOException;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;


@ClientEndpoint
public class SideCarClient {
	
	private static final Logger log =  LoggerFactory.getLogger(SideCarClient.class);
	
	Session session;

	public SideCarClient(String sideCarServerHost, String sideCarServerPort) throws Exception {
	      WebSocketContainer container = ContainerProvider.getWebSocketContainer();
	      log.info("Connecting to server: {}", "ws://172.19.0.2:5678");
	      session = container.connectToServer(SideCarClient.class, new URI("ws://172.19.0.2:5678"));
	}
	
	@OnOpen
	  public void onOpen(Session session) {
	    System.out.println("Connected to endpoint: " + session.getBasicRemote());
	    try {
	      String subscriptionRequest = "{   \"jsonrpc\": \"2.0\",   \"method\": \"subscribe_account\",   \"params\": {  \"eventType\":\"*\",   \"accounts\": [       \"http://ec2-35-166-189-14.us-west-2.compute.amazonaws.com:8088/ilp/ledger/v1/accounts/dfsp1-connector-30mins\"     ]   },   \"id\": 1 }";
	      log.info("Sending message to endpoint: {}", subscriptionRequest);
	      session.getBasicRemote().sendText(subscriptionRequest);
	    } catch (IOException ex) {
	      log.error("error", ex);
	    }
	  }

	  @OnMessage
	  public void processMessage(String message) {
	    log.info("Received message in client: " + message);
	  }
	  
	  @OnClose
	  public void processClose(CloseReason reason){
		  log.info("Closing the socket {}", reason.toString());
	  }
	  
	public void logForensicEntry(String entry) throws IOException {
		session.getBasicRemote().sendText(entry);
	}
	
	public static void main(String[] args) throws Exception {
		SideCarClient sideCarClient = new SideCarClient("172.19.0.2","5678");
		sideCarClient.logForensicEntry("Hello");
	}
}
