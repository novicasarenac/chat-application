package beans;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import jms_messages.UserRequestMessage;
import jms_messages.UserRequestMessageType;
import jms_messages.UserResponseMessage;

@Stateless
public class UserRequestSender implements UserRequestSenderLocal {
	
	@EJB
	UserResponseTransferLocal userResponseTransfer;
	
	@Inject
	JMSContext context;

	@Resource(mappedName = "java:/jms/queue/userRequest")
	private Destination destination;
	
	@Override
	public void sendRequest(UserRequestMessage userRequestMessage) {
		sendViaREST(userRequestMessage);
	}
	
	public void sendViaJMS(UserRequestMessage userRequestMessage) {
		try {
			ObjectMessage message = context.createObjectMessage();
			message.setObject(userRequestMessage);
			JMSProducer producer = context.createProducer();
			producer.send(destination, message);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	public void sendViaREST(UserRequestMessage userRequestMessage) {
		ResteasyClient client = new ResteasyClientBuilder().build();
		Response response = null;
		switch(userRequestMessage.getType()) {
			case LOGIN: {
				ResteasyWebTarget target = client.target("http://localhost:8080/UserAppWeb/rest/user/login");
				response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(userRequestMessage, MediaType.APPLICATION_JSON));
				break;
			}
			case REGISTER: {
				ResteasyWebTarget target = client.target("http://localhost:8080/UserAppWeb/rest/user/register");
				response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(userRequestMessage, MediaType.APPLICATION_JSON));
				break;
			}
			case LOGOUT: {
				ResteasyWebTarget target = client.target("http://localhost:8080/UserAppWeb/rest/user/logout");
				response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(userRequestMessage, MediaType.APPLICATION_JSON));
				break;
			}
			case GETALLUSERS: {
				ResteasyWebTarget target = client.target("http://localhost:8080/UserAppWeb/rest/user/getAllUsers/" + userRequestMessage.getSessionId());
				response = target.request(MediaType.APPLICATION_JSON).get();
				break;
			}
		}
		UserResponseMessage message = response.readEntity(UserResponseMessage.class);

		userResponseTransfer.sendMessageToWebApp(message);
	}

	public UserRequestSender() {
		super();
	}
}
