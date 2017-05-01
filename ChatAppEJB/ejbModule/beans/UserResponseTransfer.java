package beans;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;

import jms_messages.UserResponseMessage;

@Stateless
public class UserResponseTransfer implements UserResponseTransferLocal{
	
	@Inject
	JMSContext context;
	
	@Resource(mappedName = "java:/jms/queue/userResponseTransfer")
	private Destination destination;
	
	@Override
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
