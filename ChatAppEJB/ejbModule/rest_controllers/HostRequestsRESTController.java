package rest_controllers;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import beans.DataManagementLocal;
import exceptions.AliasExistsException;
import model.Host;

@Stateless
@Path("/host")
public class HostRequestsRESTController {
	
	@EJB
	DataManagementLocal dataManagement;

	@POST
	@Path("/register/{alias}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Host> register(@PathParam("alias") String alias, String address) {
		List<Host> returnValue = new ArrayList<>();
		try{
			returnValue = dataManagement.register(new Host(address, alias));
		} catch(AliasExistsException e) {
			return null;
		}
		
		return returnValue;
	}
	
	@DELETE
	public void unregister(Host host) {
		dataManagement.unregister(host);
	}
}
