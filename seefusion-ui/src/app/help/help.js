angular.module('help', ['security.authorization'])

.config(['$stateProvider', 'securityAuthorizationProvider', function ($stateProvider, securityAuthorizationProvider) {
	$stateProvider.state('help', {
		url: '/help',
		breadcrumb:'Help and Support',
		templateUrl:'help/help.tpl.html',
		controller:'HelpCtrl',
		resolve: {
			authenticatedUser: securityAuthorizationProvider.isAuthenticated
		}
	})
	;
}])

.controller('HelpCtrl', ['$scope','$log', '$state', 'ConfigService', function ($scope, $log, $state, ConfigService) {

	// Make the $state available to the template. 
	$scope.$state = $state;

	$scope.about = {};

	ConfigService.about().then(function(response){
		$scope.about = response;
	});

}]); 