package beans;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Singleton;

import exceptions.AliasExistsException;
import model.Host;

@Singleton
public class DataManagement implements DataManagementLocal {

	private Map<String, Host> hosts = new HashMap<>();
	
	@Override
	public List<Host> register(Host newHost) throws AliasExistsException{
		if(hosts.containsKey(newHost.getAlias()))
			throw new AliasExistsException();
		
		hosts.put(newHost.getAlias(), newHost);
		return (List<Host>) hosts.values();
	}

	@Override
	public void unregister(Host host) {
		hosts.remove(host.getAlias());
	}
	
}
