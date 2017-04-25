package beans;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;

import jms_messages.UserRequestMessage;

@Stateless
public class JMSRequestSender implements JMSRequestSenderLocal {
	
	@Inject
	JMSContext context;

	@Resource(mappedName = "java:/jms/queue/userRequest")
	private Destination destination;
	
	@Override
	public void sendRequest(UserRequestMessage userRequestMessage) {
		try {
			ObjectMessage message = context.createObjectMessage();
			message.setObject(userRequestMessage);
			JMSProducer producer = context.createProducer();
			producer.send(destination, message);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public JMSRequestSender() {
		super();
	}
}
