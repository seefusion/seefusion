angular.module('snapshot', ['security.authorization'])

.config(['$stateProvider', 'securityAuthorizationProvider', function ($stateProvider, securityAuthorizationProvider) {
	$stateProvider.state('snapshot', {
		url: '/snapshot/:timestamp',
		templateUrl:'snapshot/snapshot.tpl.html',
		controller:'SnapshotCtrl',
		resolve: {
			authenticatedUser: securityAuthorizationProvider.isAuthenticated,
			timestamp:['$stateParams', function($stateParams){
				return $stateParams.timestamp;
			}]
		}
	})
		.state('snapshot.seestack',{
			url:'/seestack',
			templateUrl:'snapshot/seestack.tpl.html',
			controller:function($scope){

			}
		})
		.state('snapshot.trace',{
			url:'/trace',
			templateUrl:'snapshot/trace.tpl.html',
			controller:function($scope){
				
			}
		})
	;
}])

.controller('SnapshotCtrl', ['$scope', '$http', '$timeout', '$log', '$location', '$state', 'HistoryService', 'ChartSettings', 'timestamp', function ($scope, $http, $timeout, $log, $location, $state, HistoryService, ChartSettings, timestamp) {

	// Make the $state available to the template. 
	$scope.$state = $state;

	HistoryService.getSnapshot(timestamp).then(function(data){
		$scope.snapshot = data.snapshot;
		$scope.seestack = data.seestack;
	});

}]); 