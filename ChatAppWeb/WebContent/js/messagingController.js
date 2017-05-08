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
	   });