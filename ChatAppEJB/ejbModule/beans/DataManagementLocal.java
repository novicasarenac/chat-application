package beans;

import java.util.List;

import javax.ejb.Local;

import exceptions.AliasExistsException;
import model.Host;

@Local
public interface DataManagementLocal {
	
	public List<Host> register(Host newHost) throws AliasExistsException;
	public void unregister(Host host);

}
