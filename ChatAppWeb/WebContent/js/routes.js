var app = angular.module('chatApplication.routes', ['ngRoute']);

app.config(['$routeProvider', '$httpProvider', function($routeProvider, $httpProvider) {
	
	$routeProvider
		.when('/', {
			templateUrl : 'html/welcome.html'
		});
}]);