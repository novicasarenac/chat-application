angular.module('chatApplication.MessagingController', [])
	   .controller('MessagingController', function($scope, $rootScope, $location) {
		   console.log(sessionStorage.loggedUser);
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
				   $scope.$apply(function() {
					   $scope.onlineUsers = payload;
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
			   try {
				   message = {
						   'username' : sessionStorage.loggedUser,
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
	   });