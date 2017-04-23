package beans;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

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
	public User register(String username, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean login(String username, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean logout(User logout) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<User> getAllUsers() {
		// TODO Auto-generated method stub
		return null;
	}

}
