package server_management;

import javax.ejb.Local;

import model.Host;

@Local
public interface ChatAppManagementLocal {
	
	public void sendRegisterRequest(String address, String alias);
	public void sendGetUsersRESTRequest();
	public void sendGetUsersJMSRequest();
	public boolean isMaster();
	public String getLocalAlias();
	public String getLocal();
	public String getMaster();
	
}
