package beans;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;

import jms_messages.UserResponseMessage;
import model.User;

@Stateless
public class UserResponseTransfer implements UserResponseTransferLocal{
	
	@Inject
	JMSContext context;
	
	@Resource(mappedName = "java:/jms/queue/userResponseTransfer")
	private Destination destination;
	
	@Resource(mappedName = "java:/jms/queue/userNotificationTransfer")
	private Destination notificationDestination;
	
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

	@Override
	public void sendNotificationMessageToWebApp(List<User> onlineUsers) {
		try {
			ObjectMessage message = context.createObjectMessage();
			message.setObject((Serializable) onlineUsers);
			JMSProducer producer = context.createProducer();
			producer.send(notificationDestination, message);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
