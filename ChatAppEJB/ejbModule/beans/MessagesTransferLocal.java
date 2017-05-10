package beans;

import javax.ejb.Local;

import model.Message;

@Local
public interface MessagesTransferLocal {

	public void sendMessage(Message message);
}
