package server_management;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ws.rs.client.Entity;
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
			System.out.println("-------------------master");
		
		portOffset = System.getProperty(SystemPropertiesKeys.OFFSET);
		if(portOffset == null) {
			portOffset = "0";
		}
		
		InetAddress address = null;
		try {
			address = InetAddress.getLocalHost();
			local = address.getHostAddress() + ':' + Integer.toString((SystemPropertiesKeys.MASTER_PORT + Integer.parseInt(portOffset)));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		localAlias = System.getProperty(SystemPropertiesKeys.ALIAS);
		if(localAlias == null)
			localAlias = address.getHostAddress();
		
		System.out.println("----------------------------------------"+local+"***********"+localAlias);
		
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
	
	//setting host list if node is not master
	private void sendRegisterRequest(String address, String alias) {
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client.target("http://"+master+":"+SystemPropertiesKeys.MASTER_PORT+"/ChatAppWeb/rest/host/register/"+alias);
		Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(address, MediaType.TEXT_PLAIN));
		ArrayList<Host> hostList = (ArrayList<Host>) response.readEntity(List.class);
		dataManagement.setHosts(hostList);
		sendGetUsersRESTRequest();
	}
	
	//setting user list if node is not master
	private void sendGetUsersRESTRequest() {
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client.target("http://"+master+":"+SystemPropertiesKeys.MASTER_PORT+"/UserAppWeb/rest/user/getAllUsers");
		Response response = target.request(MediaType.APPLICATION_JSON).get();
		ArrayList<User> userList = (ArrayList<User>) response.readEntity(List.class);
		dataManagement.setUsers(userList);
	}
	
	//sending request if node is master
	private void sendGetUsersJMSRequest() {
		UserRequestMessage userRequestMessage = new UserRequestMessage();
		userRequestMessage.setType(UserRequestMessageType.GETALLUSERS);
		userRequestSender.sendViaJMS(userRequestMessage);
	}

	public boolean isMaster() {
		return master == null;
	}
	
}
