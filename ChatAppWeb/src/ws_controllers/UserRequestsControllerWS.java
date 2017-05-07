package ws_controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.codehaus.jackson.map.ObjectMapper;

import beans.UserRequestSenderLocal;
import jms_messages.UserRequestMessage;
import jms_messages.UserRequestMessageType;
import jms_messages.UserResponseMessage;
import model.Host;
import server_management.ChatAppManagementLocal;
import utils.WSMessage;

@ServerEndpoint("/userRequest")
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/queue/userResponseTransfer" )
})
public class UserRequestsControllerWS implements MessageListener{

	List<Session> sessions = new ArrayList<>();
	
	@EJB
	UserRequestSenderLocal userRequestSender;
	
	@EJB
	ChatAppManagementLocal chatAppManagement;
	
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
		userRequestMessage.setSessionId(session.getId());
		if(wsmessage.getType() == UserRequestMessageType.REGISTER) {
			userRequestMessage.setType(UserRequestMessageType.REGISTER);
			userRequestMessage.setUsername(wsmessage.getUsername());
			userRequestMessage.setPassword(wsmessage.getPassword());
		} else if(wsmessage.getType() == UserRequestMessageType.LOGIN) {
			userRequestMessage.setType(UserRequestMessageType.LOGIN);
			userRequestMessage.setUsername(wsmessage.getUsername());
			userRequestMessage.setPassword(wsmessage.getPassword());
			userRequestMessage.setHost(new Host(chatAppManagement.getLocal(), chatAppManagement.getLocalAlias()));
		} else if(wsmessage.getType() == UserRequestMessageType.LOGOUT) {
			userRequestMessage.setType(UserRequestMessageType.LOGOUT);
			userRequestMessage.setUsername(wsmessage.getUsername());
			userRequestMessage.setPassword(wsmessage.getPassword());
		} else if(wsmessage.getType() == UserRequestMessageType.GETALLUSERS) {
			userRequestMessage.setType(UserRequestMessageType.GETALLUSERS);
		}
		
		userRequestSender.sendRequest(userRequestMessage);
	}
	
	public void processResponse(UserResponseMessage userResponseMessage) {
		Session session = null;
		for(Session s : sessions) {
			if(s.getId().equals(userResponseMessage.getSessionId()))
				session = s;
		}
		switch(userResponseMessage.getUserResponseStatus()) {
			case LOGGED_ON: {
				try {
					ObjectMapper mapper = new ObjectMapper();
					String jsonObject = mapper.writeValueAsString(userResponseMessage);
					session.getBasicRemote().sendText(jsonObject);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
			case INVALID_CREDENTIALS: {
				try {
					ObjectMapper mapper = new ObjectMapper();
					String jsonObject = userResponseMessage.getUserResponseStatus().toString();
					session.getBasicRemote().sendText(jsonObject);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
			case ALREADY_LOGGED: {
				try {
					ObjectMapper mapper = new ObjectMapper();
					String jsonObject = userResponseMessage.getUserResponseStatus().toString();
					session.getBasicRemote().sendText(jsonObject);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
			case REGISTERED: {
				try {
					ObjectMapper mapper = new ObjectMapper();
					String jsonObject = mapper.writeValueAsString(userResponseMessage);
					session.getBasicRemote().sendText(jsonObject);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
			case USERNAME_EXISTS: {
				try {
					ObjectMapper mapper = new ObjectMapper();
					String jsonObject = userResponseMessage.getUserResponseStatus().toString();
					session.getBasicRemote().sendText(jsonObject);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
			case ALL_USERS: {
				try {
					ObjectMapper mapper = new ObjectMapper();
					String jsonObject = mapper.writeValueAsString(userResponseMessage);
					session.getBasicRemote().sendText(jsonObject);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
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

	//user response message listener
	@Override
	public void onMessage(Message message) {
		ObjectMessage objectMessage = (ObjectMessage) message;
		try {
			UserResponseMessage userResponseMessage = (UserResponseMessage) objectMessage.getObject();
			processResponse(userResponseMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
