package beans;

import java.util.List;

import javax.ejb.Local;

import exceptions.AliasExistsException;
import model.Host;
import model.User;

@Local
public interface DataManagementLocal {
	
	public List<Host> register(Host newHost) throws AliasExistsException;
	public void unregister(String host);
	public void setHosts(List<Host> newHosts);
	public List<Host> getHosts();
	public void setUsers(List<User> newUsers);
	public void sendRegisterToAllNodes(Host newHost, String masterAlias);
	public void sendUnregisterToAllNodes(String hostAlias, String masterAlias);
	public void addUserOnline(User user);
	public List<User> getUsersOnline();
	public void removeUserOnline(User user);
	
}
