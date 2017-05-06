package jms_messages;

import java.io.Serializable;

import model.User;

public class UserNotification implements Serializable {

	private User user;
	private UserNotificationType type;

	public UserNotification() {
	}

	public UserNotification(User user, UserNotificationType type) {
		super();
		this.user = user;
		this.type = type;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public UserNotificationType getType() {
		return type;
	}

	public void setType(UserNotificationType type) {
		this.type = type;
	}
	
}
