package ws_controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.codehaus.jackson.map.ObjectMapper;

import beans.JMSRequestSenderLocal;
import jms_messages.UserRequestMessage;
import jms_messages.UserRequestMessageType;
import utils.WSMessage;

@ServerEndpoint("/userRequest")
@Singleton
public class UserRequestsControllerWS {

	List<Session> sessions = new ArrayList<>();
	
	@EJB
	JMSRequestSenderLocal jmsSender;
	
	@OnOpen
	public void onOpen(Session session) {
		if(!sessions.contains(session)) {
			sessions.add(session);
		}
	}
	
	@OnMessage
	public void onMessage(Session session, String message, boolean last) {
		WSMessage wsmessage = null;
		if(session.isOpen()) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				wsmessage = mapper.readValue(message, WSMessage.class);
				processMessage(wsmessage, session);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void processMessage(WSMessage wsmessage, Session session) {
		UserRequestMessage userRequestMessage = new UserRequestMessage();
		sessions.add(session);
		userRequestMessage.setSessionId(session.getId());
		if(wsmessage.getType() == UserRequestMessageType.REGISTER) {
			userRequestMessage.setType(UserRequestMessageType.REGISTER);
			userRequestMessage.setUsername(wsmessage.getUsername());
			userRequestMessage.setPassword(wsmessage.getPassword());
		} else if(wsmessage.getType() == UserRequestMessageType.LOGIN) {
			userRequestMessage.setType(UserRequestMessageType.LOGIN);
			userRequestMessage.setUsername(wsmessage.getUsername());
			userRequestMessage.setPassword(wsmessage.getPassword());
		} else if(wsmessage.getType() == UserRequestMessageType.LOGOUT) {
			userRequestMessage.setType(UserRequestMessageType.LOGOUT);
			userRequestMessage.setUsername(wsmessage.getUsername());
			userRequestMessage.setPassword(wsmessage.getPassword());
		} else if(wsmessage.getType() == UserRequestMessageType.GETALLUSERS) {
			userRequestMessage.setType(UserRequestMessageType.GETALLUSERS);
		}
		
		jmsSender.sendRequest(userRequestMessage);
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
