package beans;

import javax.ejb.Local;

import model.User;

@Local
public interface ResponseSenderLocal {
	
	public void sendResponse(User user);
}
