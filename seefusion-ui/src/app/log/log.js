angular.module('log', ['security.authorization'])

.config(['$stateProvider', 'securityAuthorizationProvider', function ($stateProvider, securityAuthorizationProvider) {
	$stateProvider.state('log', {
		url: '/log',
		breadcrumb:'SeeFusion Event Log',
		templateUrl:'log/log.tpl.html',
		controller:'LogCtrl',
		resolve: {
			authenticatedUser: securityAuthorizationProvider.isAuthenticated
		}
	})
	;
}])

.controller('LogCtrl', ['$scope', '$timeout', '$log', '$state', 'LogService', function ($scope, $timeout, $log, $state, LogService) {

	// Make the $state available to the template. 
	$scope.$state = $state;

	LogService.getLog().then(function(response){
		$scope.log = response;
	});

}]); 