package beans;

import java.util.List;

import javax.ejb.Local;

import jms_messages.UserResponseMessage;
import model.User;

@Local
public interface UserResponseTransferLocal {

	public void sendMessageToWebApp(UserResponseMessage userResponseMessage);
	public void sendNotificationMessageToWebApp(List<User> onlineUsers);
}
