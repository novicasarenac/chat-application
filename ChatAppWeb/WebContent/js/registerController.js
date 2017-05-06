angular.module('chatApplication.RegisterController', [])
       .controller('RegisterController', function($scope, $rootScope, $location) {
    	   var url = window.location;
    	   var host = "ws://" + url.hostname + ":" + url.port + "/ChatAppWeb/userRequest";
		   try {
			   socket = new WebSocket(host);
			   
			   socket.onopen = function() {
				   console.log("Socket connection opened");
			   }
			   
			   socket.onmessage = function(message) {
				   if(message.data === 'USERNAME_EXISTS') {
					   alert('Username exists. Please choose another one!')
				   } else {
					   $rootScope.$apply(function() {
						   $location.path('/welcome');
					   });
				   }
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