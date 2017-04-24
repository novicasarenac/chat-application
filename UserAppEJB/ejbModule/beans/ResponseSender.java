package beans;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;

import model.User;

@Stateless
public class ResponseSender implements ResponseSenderLocal {
	
	@Inject
	JMSContext context;

	@Resource(mappedName = "java:/jms/queue/userResponse")
	private Destination destination;
	
	@Override
	public void sendResponse(User user) {
		try {
			ObjectMessage message = context.createObjectMessage();
			message.setObject(user);
			JMSProducer producer = context.createProducer();
			producer.send(destination, message);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public ResponseSender() {
		super();
	}
	
}
