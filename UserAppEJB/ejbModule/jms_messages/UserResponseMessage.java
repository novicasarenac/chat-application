package jms_messages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import model.User;

public class UserResponseMessage implements Serializable {

	private User user;
	private List<User> allUsers = new ArrayList<User>();
	private String sessionId;
	private UserResponseStatus userResponseStatus;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<User> getAllUsers() {
		return allUsers;
	}

	public void setAllUsers(List<User> allUsers) {
		this.allUsers = allUsers;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public UserResponseStatus getUserResponseStatus() {
		return userResponseStatus;
	}

	public void setUserResponseStatus(UserResponseStatus userResponseStatus) {
		this.userResponseStatus = userResponseStatus;
	}

	public UserResponseMessage() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UserResponseMessage(User user, List<User> allUsers, String sessionId,
			UserResponseStatus userResponseStatus) {
		super();
		this.user = user;
		this.allUsers = allUsers;
		this.sessionId = sessionId;
		this.userResponseStatus = userResponseStatus;
	}

	public UserResponseMessage(User user, String sessionId, UserResponseStatus userResponseStatus) {
		super();
		this.user = user;
		this.sessionId = sessionId;
		this.userResponseStatus = userResponseStatus;
	}

	public UserResponseMessage(List<User> allUsers, String sessionId, UserResponseStatus userResponseStatus) {
		super();
		this.allUsers = allUsers;
		this.sessionId = sessionId;
		this.userResponseStatus = userResponseStatus;
	}
	
}
