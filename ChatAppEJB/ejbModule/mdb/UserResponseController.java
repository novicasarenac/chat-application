package mdb;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import beans.DataManagementLocal;
import beans.UserResponseTransferLocal;
import jms_messages.UserResponseMessage;
import jms_messages.UserResponseStatus;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/queue/userResponse" )
})
public class UserResponseController implements MessageListener {
	
	@EJB
	UserResponseTransferLocal userResponseTransfer;
	
	@EJB
	DataManagementLocal dataManagement;
	
	@Override
	public void onMessage(Message message) {
		ObjectMessage objectMessage = (ObjectMessage) message;
		try{
			UserResponseMessage userResponseMessage = (UserResponseMessage) objectMessage.getObject();
			if(userResponseMessage.getUserResponseStatus().equals(UserResponseStatus.ALL_USERS))
				dataManagement.setUsers(userResponseMessage.getAllUsers());
			else
				userResponseTransfer.sendMessageToWebApp(userResponseMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

}
