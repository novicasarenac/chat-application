package rest_controllers;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import beans.DataManagementLocal;
import model.User;

@Stateless
@Path("/notification")
public class UserNotificationRESTController {
	
	@EJB
	DataManagementLocal dataManagement;
	
	@POST
	@Path("/addUser")
	@Consumes(MediaType.APPLICATION_JSON)
	public void addUser(User user) {
		dataManagement.addUserOnline(user);
		return;
	}
	
	@PUT
	@Path("/removeUser")
	@Consumes(MediaType.APPLICATION_JSON)
	public void removeUser(User user) {
		dataManagement.removeUserOnline(user);
		return;
	}

}
