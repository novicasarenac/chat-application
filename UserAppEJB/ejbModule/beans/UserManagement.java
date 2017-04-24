package beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import exceptions.AlreadyLoggedOn;
import exceptions.InvalidCredentialsException;
import exceptions.UsernameExistsException;
import model.Host;
import model.User;

@Singleton
@Startup
public class UserManagement implements UserManagementLocal {

	private Map<String, User> usersOffline;
	private Map<String, User> usersOnline;
	
	@PostConstruct
	public void initialize() {
		usersOffline = new HashMap<>();
		usersOnline = new HashMap<>();
		
		for(int i = 0; i < 10; i++) {
			usersOffline.put("user"+i, new User("user"+i, "user"));
		}
	}
	
	@Override
	public User register(String username, String password) throws UsernameExistsException {
		User newUser = new User();
		if(usersOffline.containsKey(username) || usersOnline.containsKey(username)) {
			throw new UsernameExistsException("Username exists");
		}
		else {
			newUser.setUsername(username);
			newUser.setPassword(password);
			usersOffline.put(username, newUser);
		}
		
		return newUser;
	}

	@Override
	public Boolean login(String username, String password, Host host) throws InvalidCredentialsException, AlreadyLoggedOn {
		if(usersOffline.containsKey(username)) {
			User loggedUser = usersOffline.get(username);
			loggedUser.setHost(host);
			usersOnline.put(loggedUser.getUsername(), loggedUser);
			usersOffline.remove(username);
		} else if(usersOnline.containsKey(username)) {
			throw new AlreadyLoggedOn("User is already logged on");
		} else throw new InvalidCredentialsException("Invalid credentials");
		
		return true;
	}

	@Override
	public Boolean logout(User logout) {
		usersOnline.remove(logout.getUsername());
		logout.setHost(null);
		usersOffline.put(logout.getUsername(), logout);
		return true;
	}

	@Override
	public List<User> getAllUsers() {
		List<User> retVal = new ArrayList<>();
		retVal.addAll(usersOffline.values());
		retVal.addAll(usersOnline.values());
		return retVal;
	}

}
