package rest_controllers;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import model.Message;

@Stateless
@Path("/publish")
public class MessagingRESTController {
	
	@Inject
	JMSContext context;

	@Resource(mappedName = "java:/jms/queue/messageTransfer")
	private Destination destination;
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void publish(Message message) {
		sendToWebApp(message);
	}
	
	public void sendToWebApp(Message message) {
		try {
			ObjectMessage objectMessage = context.createObjectMessage();
			objectMessage.setObject(message);
			JMSProducer producer = context.createProducer();
			producer.send(destination, objectMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
