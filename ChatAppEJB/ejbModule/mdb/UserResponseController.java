package mdb;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import jms_messages.UserResponseMessage;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/queue/userResponse" )
})
public class UserResponseController implements MessageListener {
	
	@Inject
	JMSContext context;
	
	@Resource(mappedName = "java:/jms/queue/userResponseTransfer")
	private Destination destination;
	
	@Override
	public void onMessage(Message message) {
		ObjectMessage objectMessage = (ObjectMessage) message;
		try{
			UserResponseMessage userResponseMessage = (UserResponseMessage) objectMessage.getObject();
			sendMessageToWebApp(userResponseMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendMessageToWebApp(UserResponseMessage userResponseMessage) {
		try {
			ObjectMessage message = context.createObjectMessage();
			message.setObject(userResponseMessage);
			JMSProducer producer = context.createProducer();
			producer.send(destination, message);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

}
