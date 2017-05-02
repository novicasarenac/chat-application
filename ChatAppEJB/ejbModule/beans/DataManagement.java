package beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Singleton;

import exceptions.AliasExistsException;
import model.Host;
import model.User;

@Singleton
public class DataManagement implements DataManagementLocal {

	private Map<String, Host> hosts = new HashMap<>();
	private List<User> users = new ArrayList<>();
	
	@Override
	public List<Host> register(Host newHost) throws AliasExistsException{
		if(hosts.containsKey(newHost.getAlias()))
			throw new AliasExistsException();
		
		hosts.put(newHost.getAlias(), newHost);
		List<Host> returnValue = new ArrayList<>();
		for(Host host : hosts.values())
			returnValue.add(host);
		return returnValue;
	}

	@Override
	public void unregister(Host host) {
		hosts.remove(host.getAlias());
	}
	
	@Override
	public void setHosts(List<Host> newHosts) {
		for(Host host : newHosts) {
			hosts.put(host.getAlias(), host);
		}
	}

	@Override
	public void setUsers(List<User> newUsers) {
		users = newUsers;
	}
}
