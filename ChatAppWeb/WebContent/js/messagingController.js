angular.module('chatApplication.MessagingController', [])
	   .controller('MessagingController', function($scope) {
		   console.log(sessionStorage.loggedUser);
	   });