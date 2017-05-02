package server_management;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton
@Startup
public class ChatAppManagement implements ChatAppManagementLocal{

	private String master;
	private String local;
	private String localAlias;
	private String portOffset;
	
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
	}
	
}
