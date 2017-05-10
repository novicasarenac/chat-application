package beans;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;

import model.Message;

@Stateless
public class MessagesTransfer implements MessagesTransferLocal {

	@Inject
	JMSContext context;
	
	@Resource(mappedName = "java:/jms/queue/messageTransfer")
	private Destination destination;
	
	@Override
	public void sendMessage(Message message) {
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
