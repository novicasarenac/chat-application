package ws_controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.Singleton;
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

import beans.DataManagementLocal;
import jms_messages.UserResponseMessage;
import model.User;

@ServerEndpoint("/getAllOnlineUsers")
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/queue/userNotificationTransfer")
})
public class UserNotificationControllerWS implements MessageListener {

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
	
	public void sendNotification(List<User> onlineUsers) {
		ObjectMapper mapper = new ObjectMapper();
		String jsonObject;
		try {
			jsonObject = mapper.writeValueAsString(onlineUsers);
			for(Session session : sessions) {
				session.getBasicRemote().sendText(jsonObject);
			}
		} catch(Exception e) {
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
	
	//user notification message listener
	@Override
	public void onMessage(Message message) {
		ObjectMessage objectMessage = (ObjectMessage) message;
		try {
			List<User> onlineUsers = (List<User>) objectMessage.getObject();
			sendNotification(onlineUsers);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
