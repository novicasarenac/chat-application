package beans;

import javax.ejb.Local;

import jms_messages.UserRequestMessage;

@Local
public interface UserRequestSenderLocal {

	void sendRequest(UserRequestMessage userRequestMessage);
}
