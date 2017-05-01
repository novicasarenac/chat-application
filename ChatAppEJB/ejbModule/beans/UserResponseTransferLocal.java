package beans;

import javax.ejb.Local;

import jms_messages.UserResponseMessage;

@Local
public interface UserResponseTransferLocal {

	public void sendMessageToWebApp(UserResponseMessage userResponseMessage);
}
