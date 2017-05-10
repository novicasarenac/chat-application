package ws_controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.codehaus.jackson.map.ObjectMapper;

import model.Message;
import server_management.ChatAppManagementLocal;

@ServerEndpoint("/publishMessage/{username}")
@Stateless
public class MessagingControllerWS {

	Map<String, Session> userSessions = new HashMap<String, Session>();
	
	@EJB
	ChatAppManagementLocal chatAppManagement;
	
	@OnOpen
	public void onOpen(Session session, @PathParam("username") String username) {
		if(!userSessions.containsKey(username)) {
			userSessions.put(username, session);
		} else {
			userSessions.remove(username);
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
				processMessage(message);
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
	
	public void processMessage(Message message) {
		if(message.getTo() == null)
			sendPublicMessage(message);
		else if(message.getTo().getHost().getAlias().equals(chatAppManagement.getLocalAlias())) 
			sendToLocalUser(message);
	}
	
	//if receiver is on the same node as sender
	public void sendToLocalUser(Message message) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			String jsonObject = mapper.writeValueAsString(message);
			Session session = userSessions.get(message.getTo().getUsername());
			session.getBasicRemote().sendText(jsonObject);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	//sending public message
	public void sendPublicMessage(Message message) {
		//messages for local users
		for(String username : userSessions.keySet()) {
			if(!username.equals(message.getFrom().getUsername())) {
				try {
					ObjectMapper mapper = new ObjectMapper();
					String jsonObject = mapper.writeValueAsString(message);
					Session session = userSessions.get(username);
					session.getBasicRemote().sendText(jsonObject);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
