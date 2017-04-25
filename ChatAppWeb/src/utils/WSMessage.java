package utils;

import java.io.Serializable;

import jms_messages.UserRequestMessageType;

public class WSMessage implements Serializable {

	private String username;
	private String password;
	private UserRequestMessageType type;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public UserRequestMessageType getType() {
		return type;
	}

	public void setType(UserRequestMessageType type) {
		this.type = type;
	}

	public WSMessage(String username, String password, UserRequestMessageType type) {
		super();
		this.username = username;
		this.password = password;
		this.type = type;
	}

	public WSMessage() {
		super();
		// TODO Auto-generated constructor stub
	}

}
