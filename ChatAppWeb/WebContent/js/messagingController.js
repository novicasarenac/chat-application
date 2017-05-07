angular.module('chatApplication.MessagingController', [])
	   .controller('MessagingController', function($scope, $rootScope, $location) {
		   console.log(sessionStorage.loggedUser);
		   var url = window.location;
		   var host = "ws://" + url.hostname + ":" + url.port + "/ChatAppWeb/getAllOnlineUsers";
		   try {
			   socketUsers = new WebSocket(host);
			   
			   socketUsers.onopen = function(message) {
				   console.log("User socket opened");
			   }
			   
			   socketUsers.onmessage = function(message) {
				   $scope.onlineUsers = message;
			   }
			   
			   socketUsers.onclose = function() {
				   socketUser = null;
				   console.log("Socket user connection closed");
			   }
		   } catch(exception) {
			   console.log("Error!");
		   }
	   });