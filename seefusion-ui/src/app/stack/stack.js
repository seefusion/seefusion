angular.module('stack', ['security.authorization'])

.config(['$stateProvider', 'securityAuthorizationProvider', function ($stateProvider, securityAuthorizationProvider) {
	$stateProvider.state('stack', {
		url: '/stack/:threadname/:requestid',
		breadcrumb:'Stack Trace',
		templateUrl:'stack/stack.tpl.html',
		controller:'StackCtrl',
		resolve: {
			authenticatedUser: securityAuthorizationProvider.isAuthenticated,
			threadname:['$stateParams', function($stateParams){
				return $stateParams.threadname;	
			}],
			requestid:['$stateParams', function($stateParams){
				return $stateParams.requestid;	
			}]
		}
	})
		.state('stack.seestack',{
			url:'/seestack',
			breadcrumb:'Enhanced Stack Trace',
			templateUrl:'stack/seestack.tpl.html',
			controller:function($scope){

			}
		})
		.state('stack.trace',{
			url:'/trace',
			breadcrumb:'Raw Stack Trace',
			templateUrl:'stack/trace.tpl.html',
			controller:function($scope){
				
			}
		})
	;
}])

.controller('StackCtrl', ['$scope', '$http', '$timeout', '$log', '$location', '$state', 'HistoryService', 'ChartSettings', 'threadname', 'requestid', function ($scope, $http, $timeout, $log, $location, $state, HistoryService, ChartSettings, threadname, requestid) {

	// Make the $state available to the template. 
	$scope.$state = $state;

	$scope.trace = {
		'threadname':threadname,
		'requestid':requestid,
		'activeOnly':false
	};
	
	$scope.getStack = function(){
		HistoryService.getStack($scope.trace.threadname,$scope.trace.requestid,$scope.trace.activeOnly).then(function(data){
			$log.info(data);
			$scope.rawstack = data.rawstack;
			$scope.seestack = data.seestack;
			$scope.stackMessage = data.message;
		});	
	};

	$scope.doClear = function() {
		$scope.rawstack = '';
		$scope.seestack = {};
	};
	
	$scope.getStack();
}]); 