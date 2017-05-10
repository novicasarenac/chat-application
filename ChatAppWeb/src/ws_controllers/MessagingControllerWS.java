package ws_controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import beans.DataManagementLocal;
import model.Host;
import model.Message;
import server_management.ChatAppManagementLocal;

@ServerEndpoint("/publishMessage/{username}")
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/queue/messageTransfer")
})
public class MessagingControllerWS implements MessageListener{

	Map<String, Session> userSessions = new HashMap<String, Session>();
	
	@EJB
	ChatAppManagementLocal chatAppManagement;
	
	@EJB
	DataManagementLocal dataManagement;
	
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
		if(message.getTo() == null) {
			sendPublicMessage(message);
			publishPublicMessage(message);
		}
		else if(message.getTo().getHost().getAlias().equals(chatAppManagement.getLocalAlias())) 
			sendPrivateMessage(message);
		else if(!message.getTo().getHost().getAlias().equals(chatAppManagement.getLocalAlias()))
			publishPrivateMessage(message);
	}
	
	public void sendPrivateMessage(Message message) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			String jsonObject = mapper.writeValueAsString(message);
			Session session = userSessions.get(message.getTo().getUsername());
			session.getBasicRemote().sendText(jsonObject);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendPublicMessage(Message message) {
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
	
	public void publishPrivateMessage(Message message) {
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client.target("http://" + message.getTo().getHost().getAddress() + "/ChatAppWeb/rest/publish");
		Response response = target.request().post(Entity.entity(message, MediaType.APPLICATION_JSON));
	}
	
	public void publishPublicMessage(Message message) {
		for(Host host : dataManagement.getHosts()) {
			if(!host.getAlias().equals(chatAppManagement.getLocalAlias())) {
				ResteasyClient client = new ResteasyClientBuilder().build();
				ResteasyWebTarget target = client.target("http://" + host.getAddress() + "/ChatAppWeb/rest/publish");
				Response response = target.request().post(Entity.entity(message, MediaType.APPLICATION_JSON));
			}
		}
	}

	@Override
	public void onMessage(javax.jms.Message arg0) {
		System.out.println("STIGLAAAAAAAAAA");
		ObjectMessage objectMessage = (ObjectMessage) arg0;
		try {
			Message message = (Message)objectMessage.getObject();
			if(message.getTo() != null)
				sendPrivateMessage(message);
			else
				sendPublicMessage(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
