package rest_controllers;

import java.util.List;

import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import beans.UserManagementLocal;
import exceptions.AlreadyLoggedOn;
import exceptions.InvalidCredentialsException;
import exceptions.UsernameExistsException;
import jms_messages.UserRequestMessage;
import jms_messages.UserResponseMessage;
import jms_messages.UserResponseStatus;
import model.User;

@Path("/user")
public class UserRequestsRESTController {

	@EJB
	UserManagementLocal userManagement;
	
	@POST
	@Path("/register")
	@Produces(MediaType.APPLICATION_JSON)
	public UserResponseMessage register(UserRequestMessage userRequestMessage) {
		User user = null;
		try {
			user = userManagement.register(userRequestMessage.getUsername(), userRequestMessage.getPassword());
			return new UserResponseMessage(user, userRequestMessage.getSessionId(), UserResponseStatus.REGISTERED);
		} catch(UsernameExistsException e) {
			return new UserResponseMessage(user, userRequestMessage.getSessionId(), UserResponseStatus.USERNAME_EXISTS);
		}
	}
	
	@POST
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	public UserResponseMessage login(UserRequestMessage userRequestMessage) {
		User user = null;
		try {
			user = userManagement.login(userRequestMessage.getUsername(), userRequestMessage.getPassword(), userRequestMessage.getHost());
			return new UserResponseMessage(user, userRequestMessage.getSessionId(), UserResponseStatus.LOGGED_ON);
		} catch(InvalidCredentialsException e) {
			return new UserResponseMessage(user, userRequestMessage.getSessionId(), UserResponseStatus.INVALID_CREDENTIALS);
		} catch (AlreadyLoggedOn e) {
			return new UserResponseMessage(user, userRequestMessage.getSessionId(), UserResponseStatus.ALREADY_LOGGED);
		}
	}
	
	@POST
	@Path("/logout")
	@Produces(MediaType.APPLICATION_JSON)
	public UserResponseMessage logout(UserRequestMessage userRequestMessage) {
		User user = userManagement.logout(new User(userRequestMessage.getUsername(), userRequestMessage.getPassword(), userRequestMessage.getHost()));
		return new UserResponseMessage(user, userRequestMessage.getSessionId(), UserResponseStatus.LOGGED_OFF);
	}
	
	@GET
	@Path("/getAllUsers")
	@Produces(MediaType.APPLICATION_JSON)
	public UserResponseMessage getAllUsers(UserRequestMessage userRequestMessage) {
		List<User> users = userManagement.getAllUsers();
		return new UserResponseMessage(users, userRequestMessage.getSessionId(), UserResponseStatus.ALL_USERS);
		
	}
}
