package jms_messages;

import java.io.Serializable;

import model.Host;

public class UserRequestMessage implements Serializable {

	private String username;
	private String password;
	private String sessionId;
	private Host host;
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
	public Host getHost() {
		return host;
	}
	public void setHost(Host host) {
		this.host = host;
	}
	public UserRequestMessageType getType() {
		return type;
	}
	public void setType(UserRequestMessageType type) {
		this.type = type;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public UserRequestMessage() {
		super();
	}
	public UserRequestMessage(String username, String password, Host host, UserRequestMessageType type) {
		super();
		this.username = username;
		this.password = password;
		this.host = host;
		this.type = type;
	}
	public UserRequestMessage(String username, String password, String sessionId, Host host,
			UserRequestMessageType type) {
		super();
		this.username = username;
		this.password = password;
		this.sessionId = sessionId;
		this.host = host;
		this.type = type;
	}
	
	
}
