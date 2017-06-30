# Chat application

Distributed chat application built using Java EE and AngularJS. Communication between nodes and applications is implemented with Jax-RS and JMS. Web Sockets are used for communication between client and server.

## Setup ##

To run application you need [WildFly](http://wildfly.org/) enterprise application server (formerly JBoss AS) and configure JMS endpoints (Queues):
```
java:/jms/queue/userRequest
java:/jms/queue/userResponse
java:/jms/queue/userResponseTransfer
java:/jms/queue/userNotification
java:/jms/queue/userNotificationTransfer
java:/jms/queue/messageTransfer
```
Publish ChatAppEnterprise.ear and UserAppEnterprise.ear to ```/deployments``` folder into Wildfly.

## Running application ##
To start master node of application navigate to ```/bin``` folder and start Wildfly:
```
.\standalone.bat --server-config=standalone-full.xml
```
To start slave node you have to set port offset using ```"jboss.socket.binding.port-offset"``` property and address of master node using ```master``` property. Also you can set (optionally) alias for node using ```alias``` property. Start Wildfly:
```
.\standalone.bat -D"jboss.socket.binding.port-offset"=100 -Dmaster="localhost" --server-config=standalone-full.xml
```
