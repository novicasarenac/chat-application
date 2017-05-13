package server_management;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import beans.DataManagementLocal;
import beans.UserRequestSenderLocal;
import exceptions.AliasExistsException;
import jms_messages.UserRequestMessage;
import jms_messages.UserRequestMessageType;
import model.Host;
import model.User;

@Singleton
@Startup
public class ChatAppManagement implements ChatAppManagementLocal{

	private String master;
	private String local;
	private String localAlias;
	private String portOffset;
	
	@EJB
	UserRequestSenderLocal userRequestSender;
	
	@EJB
	DataManagementLocal dataManagement;
	
	@PostConstruct
	public void initialize() {
		master = System.getProperty(SystemPropertiesKeys.MASTER_NODE);
		
		if(master == null)
			System.out.println("This is master node!");
		else 
			System.out.println("Master node: "+master);
		
		portOffset = System.getProperty(SystemPropertiesKeys.OFFSET);
		if(portOffset == null) {
			portOffset = "0";
		}
		
		InetAddress address = null;
		try {
			address = InetAddress.getLoopbackAddress();		//for ip address getLocalHost()
			local = address.getHostAddress() + ':' + Integer.toString((SystemPropertiesKeys.MASTER_PORT + Integer.parseInt(portOffset)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		localAlias = System.getProperty(SystemPropertiesKeys.ALIAS);
		if(localAlias == null)
			localAlias = address.getHostName() + portOffset;
		
		System.out.println("Local address: "+local+"\tLocal alias: "+localAlias);
		
		if(isMaster()) {
			try {
				dataManagement.register(new Host(local, localAlias));
				sendGetUsersJMSRequest();
			} catch (AliasExistsException e) {
				e.printStackTrace();
			}
		} else {
			sendRegisterRequest(local, localAlias);
		}
	}
	
	@PreDestroy
	public void preDestroy() {
		if(!isMaster()) {
			ResteasyClient client = new ResteasyClientBuilder().build();
			String path = "http://" + master + ":" + SystemPropertiesKeys.MASTER_PORT + "/ChatAppWeb/rest/host/unregister";
			ResteasyWebTarget target = client.target(path);
			target.request().post(Entity.entity(localAlias, MediaType.TEXT_PLAIN));
		}
	}
	
	//setting host list if node is not master
	@Override
	public void sendRegisterRequest(String address, String alias) {
		ResteasyClient client = new ResteasyClientBuilder().build();
		String path = "http://"+master+":"+SystemPropertiesKeys.MASTER_PORT+"/ChatAppWeb/rest/host/register/"+alias;
		System.out.println("PATH: " + path);
		ResteasyWebTarget target = client.target(path);
		Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(address, MediaType.TEXT_PLAIN));
		ArrayList<Host> hostList = (ArrayList<Host>) response.readEntity(new GenericType<List<Host>>() { });
		
		for(Host h : hostList)
			System.out.println("Address: "+h.getAddress()+"\tAlias: "+h.getAlias());
		
		dataManagement.setHosts(hostList);
		sendGetUsersRESTRequest();
	}
	
	//setting user list if node is not master
	@Override
	public void sendGetUsersRESTRequest() {
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client.target("http://"+master+":"+SystemPropertiesKeys.MASTER_PORT+"/UserAppWeb/rest/user/getAllUsers");
		Response response = target.request(MediaType.APPLICATION_JSON).get();
		ArrayList<User> userList = (ArrayList<User>) response.readEntity(new GenericType<List<User>>() { });
		dataManagement.setUsers(userList);
	}
	
	//sending request if node is master
	@Override
	public void sendGetUsersJMSRequest() {
		UserRequestMessage userRequestMessage = new UserRequestMessage();
		userRequestMessage.setType(UserRequestMessageType.GETALLUSERS);
		userRequestSender.sendViaJMS(userRequestMessage);
	}

	@Override
	public boolean isMaster() {
		return master == null;
	}

	@Override
	public String getLocalAlias() {
		return localAlias;
	}

	@Override
	public String getLocal() {
		return local;
	}

	@Override
	public String getMaster() {
		return master;
	}

}
