angular.module('chatApplication.RegisterController', [])
       .controller('RegisterController', function($scope, $rootScope, $location) {
    	   var host = "ws://localhost:8080/ChatAppWeb/userRequest";
		   try {
			   socket = new WebSocket(host);
			   
			   socket.onopen = function() {
				   console.log("Socket connection opened");
			   }
			   
			   socket.onmessage = function(message) {
				   $rootScope.$apply(function() {
					   $location.path('/welcome');
				   });
			   }
			   
			   socket.onclose = function() {
				   socket = null;
				   console.log("socket connection closed");
			   }
		   } catch(exception) {
			   console.log("Error!");
		   }
		   
		   function send(userToRegister) {
			   try {
				   message = {
						   'username' : userToRegister.username,
						   'password' : userToRegister.password,
						   'type' : 'REGISTER'
				   };
				   messageToSent = JSON.stringify(message);
				   socket.send(messageToSent);
				   console.log("message sent");
			   } catch(exception) {
				   console.log("message sending failed");
			   }
		   }
		   
		   $scope.register = function(user) {
			   send(user);
		   }
       });