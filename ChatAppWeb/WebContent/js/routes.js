var app = angular.module('chatApplication.routes', ['ngRoute']);

app.config(['$routeProvider', '$httpProvider', function($routeProvider, $httpProvider) {
	
	$routeProvider
		.when('/welcome', {
			templateUrl : 'html/welcome.html'
		})
		.when('/login', {
			templateUrl : 'html/login.html'
		})
		.when('/register', {
			templateUrl : 'html/register.html'
		})
		.when('/messaging', {
			templateUrl : 'html/messaging.html'
		});
}]);