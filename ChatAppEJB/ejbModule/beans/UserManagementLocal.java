package beans;

import java.util.List;

import javax.ejb.Local;

import model.User;

@Local
public interface UserManagementLocal {
	
	public User register(String username, String password);
	public Boolean login(String username, String password);
	public Boolean logout(User logout);
	public List<User> getAllUsers();
}
