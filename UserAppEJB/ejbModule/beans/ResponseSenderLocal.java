package beans;

import javax.ejb.Local;

import jms_messages.UserNotification;
import jms_messages.UserResponseMessage;
import model.User;

@Local
public interface ResponseSenderLocal {
	
	void sendResponse(UserResponseMessage userResponseMessage);
	void sendUserNotification(UserNotification notification);
}
