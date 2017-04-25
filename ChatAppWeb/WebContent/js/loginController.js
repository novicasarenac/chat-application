angular.module('chatApplication.LoginController', [])
	   .controller('LoginController', function($scope) {
		   var host = "ws://localhost:8080/ChatAppWeb/userRequest";
		   try {
			   socket = new WebSocket(host);
			   
			   socket.onopen = function() {
				   console.log("Socket connection opened");
			   }
			   
			   socket.onmessage = function() {
				   console.log("message received");
			   }
			   
			   socket.onclose = function() {
				   socket = null;
				   console.log("socket connection closed");
			   }
		   } catch(exception) {
			   console.log("Error!");
		   }
		   
		   function send(userToLogin) {
			   try {
				   message = {
						   'username' : userToLogin.username,
						   'password' : userToLogin.password,
						   'type' : 'LOGIN'
				   };
				   messageToSent = JSON.stringify(message);
				   socket.send(messageToSent);
				   console.log("message sent");
			   } catch(exception) {
				   console.log("message sending failed");
			   }
		   }
		   
		   $scope.login = function(user) {
			   send(user);
		   }
	   });