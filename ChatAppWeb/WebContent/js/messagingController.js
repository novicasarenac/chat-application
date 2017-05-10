angular.module('chatApplication.MessagingController', [])
	   .controller('MessagingController', function($scope, $rootScope, $location) {
		   $scope.onlineUsers = [];
		   var url = window.location;
		   var host = "ws://" + url.hostname + ":" + url.port + "/ChatAppWeb/getAllOnlineUsers";
		   try {
			   socketUsers = new WebSocket(host);
			   
			   socketUsers.onopen = function(message) {
				   console.log("User socket opened");
			   }
			   
			   socketUsers.onmessage = function(message) {
				   var payload = JSON.parse(message.data);
				   var currentlyLogged = JSON.parse(sessionStorage.loggedUser);
				   var temp = payload.filter(user => user.username != currentlyLogged.username);
				   $scope.$apply(function() {
					   $scope.onlineUsers = temp;
				   })
			   }
			   
			   socketUsers.onclose = function() {
				   socketUser = null;
				   console.log("Socket user connection closed");
			   }
		   } catch(exception) {
			   console.log("Error!");
		   }
		   
		   //logout
		   var hostLogout = "ws://" + url.hostname + ":" + url.port + "/ChatAppWeb/userRequest";
		   try {
			   socketLogout = new WebSocket(hostLogout);
			   
			   socketLogout.onopen = function() {
				   
			   }
			   
			   socketLogout.onmessage = function(message) {
				   var payload = JSON.parse(message.data);
				   if(payload.userResponseStatus == 'LOGGED_OFF') {
					   sessionStorage.removeItem('loggedUser');
					   $rootScope.$apply(function() {
						   $location.path('/login');
					   });
				   }
			   }
			   
			   socket.onclose = function() {
				   socket = null;
			   }
		   } catch(exception) {
			   console.log("Error!");
		   }
		   
		   function send() {
			   var logged = JSON.parse(sessionStorage.loggedUser)
			   try {
				   message = {
						   'username' : logged.username,
						   'password' : null,
						   'type' : 'LOGOUT'
				   };
				   messageToSent = JSON.stringify(message);
				   socketLogout.send(messageToSent);
				   console.log("message sent");
			   } catch(exception) {
				   console.log("message sending failed");
			   }
		   }
		   
		   $scope.logout = function() {
			   send();
		   }
		   
		   //messaging
		   var currentLoggedUser = JSON.parse(sessionStorage.loggedUser);
		   var messagingSocketUrl = "ws://" + url.hostname + ":" + url.port + "/ChatAppWeb/publishMessage/" + currentLoggedUser.username;
		   
		   try {
			   messagingSocket = new WebSocket(messagingSocketUrl);
			   
			   messagingSocket.onopen = function() {
				   
			   }
			   
			   messagingSocket.onmessage = function(messageString) {
				   var message = JSON.parse(messageString.data);
				   if(message.to == null) {
					   $scope.publicMessages.push(message);
					   if($scope.currentChatUser == null) {
						   $scope.$apply(function() {
							   $scope.currentMessages.push(message);
						   })
					   }
				   }else {
					   $scope.privateMessages.push(message);
					   if($scope.currentChatUser != null) {
						   if($scope.currentChatUser.username == message.from.username) {
							   $scope.$apply(function() {
								   $scope.currentMessages.push(message);
							   })
						   }
					   }
				   }
			   }
			   
			   messagingSocket.onclose = function() {
				   socket = null;
				   console.log("socket connection closed");
			   }
		   } catch(exception) {
			   console.log("Error!");
		   }
		   
		   $scope.currentChatUser = null;
		   
		   $scope.currentMessages = [];
		   
		   $scope.privateMessages = [];
		   
		   $scope.publicMessages = [];
		   
		   var setCurrentMessages = function(user) {
			   var loggedUser = JSON.parse(sessionStorage.loggedUser);
			   $scope.currentMessages = $scope.privateMessages.filter(message => (message.to.username == user.username || message.from.username == user.username));
		   }
		   
		   $scope.showPublicMessages = function() {
			   $scope.currentChatUser = null;
			   $scope.currentMessages = $scope.publicMessages.filter(message => message.to == null);
		   }
		   
		   $scope.switchUser = function(user) {
			   $scope.currentChatUser = user;
			   setCurrentMessages(user);
		   }

		   $scope.sendMessage = function(content) {
			   var messageForSending = {
					   'from' : JSON.parse(sessionStorage.loggedUser),
					   'to' : $scope.currentChatUser,
					   'date' : new Date(),
					   'subject' : null,
					   'content' : content
			   };
			   
			   if(messageForSending.to == null) {
				   $scope.publicMessages.push(messageForSending);
			   } else {
				   $scope.privateMessages.push(messageForSending);
			   }
			   $scope.currentMessages.push(messageForSending);
			   
			   try {
				   var messageJSON = angular.toJson(messageForSending);
				   messagingSocket.send(messageJSON);
			   } catch(exception) {
				   console.log("Error!");
			   }
		   }
	   });