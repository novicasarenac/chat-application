package ws_controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.codehaus.jackson.map.ObjectMapper;

import model.Message;

@ServerEndpoint("/publishMessage/{username}")
public class MessagingControllerWS {

	Map<String, Session> userSessions = new HashMap<String, Session>();
	
	@OnOpen
	public void onOpen(Session session, @PathParam("username") String username) {
		if(!userSessions.containsKey(username)) {
			userSessions.put(username, session);
		}
	}
	
	@OnMessage
	public void onMessage(Session session, String messageString, @PathParam("username") String username, boolean last) {
		Message message = null;
		if(session.isOpen()) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				message = mapper.readValue(messageString, Message.class);
				System.out.println("Stigla poruka:" + message.toString());
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@OnError
	public void onError(Session session, @PathParam("username") String username, Throwable t) {
		userSessions.remove(username);
		t.printStackTrace();
	}
	
	@OnClose
	public void onClose(Session session, @PathParam("username") String username) {
		userSessions.remove(username);
	}
}
