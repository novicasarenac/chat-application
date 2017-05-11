package beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import exceptions.AliasExistsException;
import model.Host;
import model.User;

@Singleton
public class DataManagement implements DataManagementLocal {

	private Map<String, Host> hosts = new HashMap<>();
	private List<User> users = new ArrayList<>();
	private List<User> usersOnline = new ArrayList<>();
	
	@EJB
	UserResponseTransferLocal userResponseTransfer;
	
	@Override
	@Lock(LockType.WRITE)
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
	public void unregister(String alias) {
		System.out.println("HOST" + alias + " removed");
		hosts.remove(alias);
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
		
		for(User user : users) {
			if(user.getHost() != null)
				usersOnline.add(user);
		}
	}
	
	//if master send register to all nodes in cluster
	@Override
	public void sendRegisterToAllNodes(Host newHost, String masterAlias) {
		for(Host host : hosts.values()) {
			if(!host.getAlias().equals(newHost.getAlias()) && !host.getAlias().equals(masterAlias)) {
				ResteasyClient client = new ResteasyClientBuilder().build();
				String path = "http://"+host.getAddress()+"/ChatAppWeb/rest/host/register/"+newHost.getAlias();
				System.out.println("PATH: " + path);
				ResteasyWebTarget target = client.target(path);
				Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(newHost.getAddress(), MediaType.TEXT_PLAIN));
			}
		}
	}
	
	//if master send unregister to all nodes in cluster
	@Override
	public void sendUnregisterToAllNodes(String hostAlias, String masterAlias) {
		for(Host host : hosts.values()) {
			if(!host.getAlias().equals(hostAlias) && !host.getAlias().equals(masterAlias)) {
				ResteasyClient client = new ResteasyClientBuilder().build();
				String path = "http://" + host.getAddress() + "/ChatAppWeb/rest/host/unregister";
				ResteasyWebTarget target = client.target(path);
				target.request().post(Entity.entity(hostAlias, MediaType.TEXT_PLAIN));
			}
		}
	}

	@Override
	public List<Host> getHosts() {
		List<Host> returnValue = new ArrayList<>();
		for(Host host : hosts.values())
			returnValue.add(host);
		
		return returnValue;
	}

	@Override
	public void addUserOnline(User user) {
		usersOnline.add(user);
		//send notification to web app
		userResponseTransfer.sendNotificationMessageToWebApp(usersOnline);
		System.out.println("User " + user.getUsername() + " is online");
	}

	@Override
	public void removeUserOnline(User user) {
		int index = -1;
		for(User u : usersOnline) {
			if(user.getUsername().equals(u.getUsername()))
				index = usersOnline.indexOf(u);
		}
		usersOnline.remove(index);
		//send notification to web app
		userResponseTransfer.sendNotificationMessageToWebApp(usersOnline);
		System.out.println("User " + user.getUsername() + " is offline now");
	}

	@Override
	public List<User> getUsersOnline() {
		return usersOnline;
	}

}
