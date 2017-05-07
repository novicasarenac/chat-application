package mdb;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import beans.DataManagementLocal;
import jms_messages.UserNotification;
import model.Host;
import server_management.ChatAppManagementLocal;

@MessageDriven(activationConfig = { 
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/queue/userNotification")
})
public class UserNotificationController implements MessageListener {

	@EJB
	DataManagementLocal dataManagement;
	
	@EJB
	ChatAppManagementLocal chatAppManagement;
	
	@Override
	public void onMessage(Message message) {
		ObjectMessage objectMessage = (ObjectMessage)message;
		try {
			UserNotification notification = (UserNotification) objectMessage.getObject();
			sendNotification(notification);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendNotification(UserNotification notification) {
		switch (notification.getType()) {
		case LOGIN: {
			dataManagement.addUserOnline(notification.getUser());
			for(Host host : dataManagement.getHosts()) {
				if(!host.getAlias().equals(chatAppManagement.getLocalAlias())) {
					ResteasyClient client = new ResteasyClientBuilder().build();
					ResteasyWebTarget target = client.target("http://" + host.getAddress() + "/ChatAppWeb/rest/notification/addUser");
					Response response = target.request().post(Entity.entity(notification.getUser(), MediaType.APPLICATION_JSON));
				}
			}
			break;
		}
		case LOGOUT:
			for(Host host : dataManagement.getHosts()) {
				dataManagement.removeUserOnline(notification.getUser());
				if(!host.getAlias().equals(chatAppManagement.getLocalAlias())) {
					ResteasyClient client = new ResteasyClientBuilder().build();
					ResteasyWebTarget target = client.target("http://" + host.getAddress() + "/ChatAppWeb/rest/notification/removeUser");
					Response response = target.request().put(Entity.entity(notification.getUser(), MediaType.APPLICATION_JSON));
				}
			}
			break;
		}
	}

}
