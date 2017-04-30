package beans;

import javax.annotation.Resource;
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
		if(userRequestMessage.getType().equals(UserRequestMessageType.LOGIN)) {
			ResteasyWebTarget target = client.target("http://127.0.0.1:8080/UserAppWeb/rest/user/login");
			Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(userRequestMessage, MediaType.APPLICATION_JSON));
			UserResponseMessage message = response.readEntity(UserResponseMessage.class);
			
			System.out.println(message.toString());
		}
	}

	public UserRequestSender() {
		super();
	}
}
