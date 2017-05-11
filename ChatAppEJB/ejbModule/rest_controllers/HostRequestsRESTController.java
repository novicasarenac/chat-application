package rest_controllers;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import beans.DataManagementLocal;
import exceptions.AliasExistsException;
import model.Host;
import model.User;
import server_management.ChatAppManagementLocal;

@Stateless
@Path("/host")
public class HostRequestsRESTController {
	
	@EJB
	DataManagementLocal dataManagement;
	
	@EJB
	ChatAppManagementLocal chatAppManagement;
	
	@POST
	@Path("/register/{alias}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	public List<Host> register(@PathParam("alias") String alias, String address) {
		List<Host> returnValue = new ArrayList<>();
		try{
			Host newHost = new Host(address, alias);
			returnValue = dataManagement.register(newHost);
			
			//if node is master, send register request to all other nodes
			if(chatAppManagement.isMaster())
				dataManagement.sendRegisterToAllNodes(newHost, chatAppManagement.getLocalAlias());
			
			System.out.println("Host " + alias + " successfully registered");
		} catch(AliasExistsException e) {
			return null;
		}
		
		return returnValue;
	}
	
	@POST
	@Path("/unregister")
	@Consumes(MediaType.TEXT_PLAIN)
	public void unregister(String alias) {
		dataManagement.unregister(alias);
		
		if(chatAppManagement.isMaster())
			dataManagement.sendUnregisterToAllNodes(alias, chatAppManagement.getLocalAlias());
	}
}
