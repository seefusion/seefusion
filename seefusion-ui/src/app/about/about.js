angular.module('about', ['security.authorization'])

.config(['$stateProvider', 'securityAuthorizationProvider', function ($stateProvider, securityAuthorizationProvider) {
	$stateProvider.state('about', {
		url: '/about',
		breadcrumb:'Information and Support',
		templateUrl:'about/about.tpl.html',
		controller:'AboutCtrl',
		resolve: {
			authenticatedUser: securityAuthorizationProvider.isAuthenticated
		}
	})
	;
}])

.controller('AboutCtrl', ['$scope','$log', '$state', '$timeout', 'ConfigService', 'ServerService', function ($scope, $log, $state, $timeout, ConfigService, ServerService) {

	// Make the $state available to the template. 
	$scope.$state = $state;

	$scope.about = {};
	$scope.seeFusionInfo = {};

	ConfigService.about().then(function(response){
		$scope.about = response;
	});

	ServerService.getSeeFusionInfo().then(function(response){
		$scope.seeFusionInfo = response;
	});

}]); 