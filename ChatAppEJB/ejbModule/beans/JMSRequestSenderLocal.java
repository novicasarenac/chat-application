package beans;

import javax.ejb.Local;

import jms_messages.UserRequestMessage;

@Local
public interface JMSRequestSenderLocal {

	void sendRequest(UserRequestMessage userRequestMessage);
}
