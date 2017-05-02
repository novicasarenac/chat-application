package beans;

import java.util.List;

import javax.ejb.Local;

import exceptions.AliasExistsException;
import model.Host;
import model.User;

@Local
public interface DataManagementLocal {
	
	public List<Host> register(Host newHost) throws AliasExistsException;
	public void unregister(Host host);
	public void setHosts(List<Host> newHosts);
	public void setUsers(List<User> newUsers);
	
}
