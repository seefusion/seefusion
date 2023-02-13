angular.module('info', [])

.config(['$stateProvider', function ($stateProvider) {
	$stateProvider.state('info', {
		url: '/info',
		breadcrumb:'Information',
		templateUrl:'info/info.tpl.html',
		controller:'InfoCtrl',
	})
	.state('info.connection',{
		url:'/connection',
		breadcrumb:'Server Connectivity Problem',
		templateUrl:'info/connectivity.tpl.html',
		controller:'ConnectionCtrl'
	})
	;
}])

.controller('InfoCtrl', ['$rootScope', '$scope','$log', '$state', 'ConfigService', function ($rootScope, $scope, $log, $state, ConfigService) {
	// Make the $state available to the template.
	$scope.$state = $state;

	$rootScope.problem = true;

}])

.controller('ConnectionCtrl', ['$rootScope', '$scope','$timeout','$log', '$state', 'ConfigService', function ($rootScope, $scope, $timeout, $log, $state, ConfigService) {
	// Make the $state available to the template.
	$scope.$state = $state;

	$rootScope.problem = true;

	// Test the connection, and if it's okay, send the user to the home page
	var testConnection = function() {
		return ConfigService.about().then(function(){
			window.location.href="/";
		});
	};

	$scope.retry = function() {
		testConnection();
	};

	testConnection();
}])

; 