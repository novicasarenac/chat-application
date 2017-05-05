package server_management;

import javax.ejb.Local;

@Local
public interface ChatAppManagementLocal {
	
	public void sendRegisterRequest(String address, String alias);
	public void sendGetUsersRESTRequest();
	public void sendGetUsersJMSRequest();
	public boolean isMaster();
	public String getLocalAlias();
	
}
