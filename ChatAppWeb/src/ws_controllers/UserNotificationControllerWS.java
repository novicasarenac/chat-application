package ws_controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.jms.Message;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.codehaus.jackson.map.ObjectMapper;

import beans.DataManagementLocal;

@ServerEndpoint("/getAllOnlineUsers")
@Singleton
public class UserNotificationControllerWS{

	List<Session> sessions = new ArrayList<>();
	
	@EJB
	DataManagementLocal dataManagement;
	
	@OnOpen
	public void onOpen(Session session) {
		if(!sessions.contains(session))
			sessions.add(session);
		
		ObjectMapper mapper = new ObjectMapper();
		String jsonObject;
		try {
			jsonObject = mapper.writeValueAsString(dataManagement.getUsersOnline());
			session.getBasicRemote().sendText(jsonObject);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@OnMessage
	public void onMessage(Session session, String message, boolean last) {
		
	}
	
	@OnClose
	public void onClose(Session session) {
		sessions.remove(session);
	}
	
	@OnError
	public void onError(Session session, Throwable t) {
		sessions.remove(session);
		t.printStackTrace();
	}
}
