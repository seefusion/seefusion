angular.module('monitoring', ['security.authorization'])

.config(['$stateProvider', 'securityAuthorizationProvider', function ($stateProvider, securityAuthorizationProvider) {
	$stateProvider.state('monitoring', {
		url: '/monitoring',
		templateUrl:'monitoring/monitoring.tpl.html',
		controller:'MonitoringCtrl',
		resolve: {
			authenticatedUser: securityAuthorizationProvider.isAuthenticated
		}
	})
		.state('monitoring.rules',{
			url:'/rules',
			breadcrumb:'Server Monitoring Rules',
			templateUrl:'monitoring/rules.tpl.html',
			controller:function($scope){

			}
		})
		.state('monitoring.add',{
			url:'/add',
			breadcrumb:'Add a Monitoring  Rule',
			templateUrl:'monitoring/edit.tpl.html',
			controller:function($scope){

			}
		})
		.state('monitoring.edit',{
			url:'/edit',
			breadcrumb:'Edit a Monitoring  Rule',
			templateUrl:'monitoring/edit.tpl.html',
			controller:function($scope){

			}
		})
		.state('monitoring.incidents',{
			url:'/incidents',
			breadcrumb:'Monitoring Incidents',
			templateUrl:'monitoring/incidents.tpl.html',
			controller:'IncidentsCtrl',
			resolve: {
				authenticatedUser: securityAuthorizationProvider.isAuthenticated
			}
		})
	;
}])

.controller('MonitoringCtrl', ['$scope', '$timeout', '$log', '$state', 'MonitoringService', function ($scope, $timeout, $log, $state, MonitoringService) {

	// Make the $state available to the template. 
	$scope.$state = $state;

	$scope.rules = [];
	$scope.ruleTypes = [];
	$scope.rule = MonitoringService.rule;
	
	var getProfile = function(){
		MonitoringService.getProfile().then(function(response){
			$scope.rules = response.rules;
			$scope.ruleTypes = response.ruletypes;
		});
	};

	getProfile();

	$scope.newRule = function(){
		$scope.rule = MonitoringService.rule;
		$state.go('monitoring.add');
	};

	$scope.editRule = function(rule){
		$scope.rule = rule;
		$state.go('monitoring.edit');
	};

	$scope.removeRule = function(rule){
		MonitoringService.remove(rule.id).then(function(response){
			$scope.rules = response.rules;
			$scope.ruleTypes = response.ruletypes;
		});
	};

	$scope.save = function(){
		MonitoringService.save($scope.rule).then(function(response){
			$scope.rules = response.rules;
			$scope.ruleTypes = response.ruletypes;
			$state.go('monitoring.rules');
		});
	};

}])
; 