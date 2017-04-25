package ws_controllers;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Singleton;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/userRequest")
@Singleton
public class UserRequestsControllerWS {

	List<Session> sessions = new ArrayList<>();
	
	@OnOpen
	public void onOpen(Session session) {
		if(!sessions.contains(session)) {
			sessions.add(session);
		}
	}
	
	@OnMessage
	public void onMessage(Session session, String message, boolean last) {
		if(session.isOpen()) {
			System.out.println("-------------------------------------Stigla poruka");
		}
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
