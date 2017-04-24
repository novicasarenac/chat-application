package beans;

import java.util.List;

import javax.ejb.Local;

import exceptions.AlreadyLoggedOn;
import exceptions.InvalidCredentialsException;
import exceptions.UsernameExistsException;
import model.Host;
import model.User;

@Local
public interface UserManagementLocal {
	
	public User register(String username, String password) throws UsernameExistsException;
	public User login(String username, String password, Host host) throws InvalidCredentialsException, AlreadyLoggedOn;
	public User logout(User logout);
	public List<User> getAllUsers();
}
