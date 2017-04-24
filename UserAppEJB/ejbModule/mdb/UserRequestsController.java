package mdb;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import beans.ResponseSenderLocal;
import beans.UserManagementLocal;
import exceptions.AlreadyLoggedOn;
import exceptions.InvalidCredentialsException;
import exceptions.UsernameExistsException;
import jms_messages.UserRequestMessage;
import model.User;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/queue/userRequest" )
})
public class UserRequestsController implements MessageListener{

	@EJB
	UserManagementLocal userManagement;
	
	@EJB
	ResponseSenderLocal responseSender;
	
	@Override
	public void onMessage(Message message) {
		ObjectMessage objectMessage = (ObjectMessage)message;
		try {
			UserRequestMessage userRequestMessage = (UserRequestMessage) objectMessage.getObject();
			switch(userRequestMessage.getType()){
				case REGISTER: {
					User user = userManagement.register(userRequestMessage.getUsername(), userRequestMessage.getPassword());
					break;
				}
				case LOGIN: {
					User user = userManagement.login(userRequestMessage.getUsername(), userRequestMessage.getPassword(), userRequestMessage.getHost());
					responseSender.sendResponse(user);
					break;
				}
				case LOGOUT: {
					User user = userManagement.logout(new User(userRequestMessage.getUsername(), userRequestMessage.getPassword(), userRequestMessage.getHost()));
					responseSender.sendResponse(user);
					break;
				}
				case GETALLUSERS: {
					
				}
					
					
			}
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UsernameExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidCredentialsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AlreadyLoggedOn e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
