angular.module('dashboard', ['security.authorization'])

.constant('pageStuff', {
    'state' : '',
    'currentState' : '',
    'statePages' : []
})

.config(['$stateProvider', 'securityAuthorizationProvider', function ($stateProvider, securityAuthorizationProvider) {
	$stateProvider.state('dashboard', {
		url: '/dashboard',
		templateUrl:'dashboard/dashboard.tpl.html',
		controller:'DashboardCtrl',
		resolve: {
			authenticatedUser: securityAuthorizationProvider.isAuthenticated
		}
	})
		.state('dashboard.all',{
			url:'/all',
			breadcrumb:'Dashboard:  All activity',
			templateUrl:'dashboard/servers.tpl.html',
			controller:'DashServerCtrl'
		})
		.state('dashboard.problems',{
			url:'/problems',
			breadcrumb:'Dashboard:  Problem activity',
			templateUrl:'dashboard/servers.tpl.html',
			controller:'DashServerCtrl'
		})
		.state('dashboard.incidents',{
			url:'/incidents',
			breadcrumb:'Dashboard:  Monitoring Incidents',
			templateUrl:'dashboard/incidents.tpl.html',
			controller:'IncidentsCtrl'
		})
		.state('dashboard.incidents.detail',{
			url:'/detail/:id',
			breadcrumb:'Monitoring Incident Details',
			templateUrl:'dashboard/incident.tpl.html',
			controller:'IncidentCtrl',
			resolve: {
				id:['$stateParams', function($stateParams){
				return $stateParams.id;
			}]
			}
		})
	;
}])

.controller('DashboardCtrl', ['$scope', '$log', '$state', function ($scope, $log, $state) {
	//
}])

.controller('DashServerCtrl', ['$rootScope', '$scope', '$interval', '$log', '$state', '$window', 'ServerService', 'filterFilter', function ($rootScope, $scope, $interval, $log, $state, $window, ServerService, filterFilter) {
	// Make the $state available to the template.
	$scope.$state = $state;

	$scope.initialLoading = true;
	$scope.servers = [];

	// initialize the timer
	var dataInterval;

	//internal variable, base refresh interval in MS
	var counterRequestTimeout = 0;

	//state
	var state = $state.current.name.split('.');
	var currentState = state[state.length - 1];

  var getData = function(){
		return ServerService.getServers(currentState).then(function(data){
			$scope.servers = data;
			$scope.initialLoading = false;
			return data;
		});
	};

	var manualRefresh = function() {
		getData();
	};

  var startInterval = function(ms) {
			if (angular.isNumber(ms) && ms > 0) {
				dataInterval = $interval(function() {
					getData();
				}, ms);
			}
	};

	var stopInterval = function() {
		if(angular.isDefined(dataInterval)) {
			$interval.cancel(dataInterval);
		}
	};

	//function to reset the refresh timeout (in seconds)
	var updateRefresh = function(refreshMS) {

		// stop/cancel the existing timer
		stopInterval();
		// if LTE zero, we won't use a timer:
		if (angular.isNumber(refreshMS) && refreshMS > 0) {
			startInterval(refreshMS);
		}
	};

	// when the global refresh interval is changed:
	$rootScope.$watch('globalRefreshInterval',function(newValue){
		// this gets called when the state starts, so it's an init, too.
		counterRequestTimeout = newValue;
		updateRefresh(newValue);
	});

	// when the global manual refresh button is clicked:
	// Fires when $rootScope.$emit('globalRefreshTrigger');
	$rootScope.$on('globalRefreshTrigger',function(event){
		manualRefresh();
	});

	$scope.openServer = function(server) {
		if(angular.isDefined(server.browserurl)) {
			$window.open(server.browserurl, '_blank');
		}
	};

	$scope.$on("$destroy",function(){
		stopInterval();
	});

	$scope.pct = function(a,b) {
		return percentCalc(a,b);
	};

	getData();
}])


.controller('IncidentsCtrl', ['$rootScope', '$scope', '$interval', '$log', '$state', 'MonitoringService', 'filterFilter', function ($rootScope, $scope, $interval, $log, $state, MonitoringService, filterFilter) {
	$scope.initialLoading = true;
	$scope.incidents = [];

	// initialize the timer
	var dataInterval;

	//internal variable, base refresh interval in MS
	var counterRequestTimeout = 0;

	//state
	var state = $state.current.name.split('.');
	var currentState = state[state.length - 1];

	//paginiation init
	$scope.pageSize = 15;
	$scope.currentPage = 1;
	$scope.searchText = '';
	$scope.noOfPages = 1;
	$scope.totalItems = 10;

	// for hightlighting the clicked incident row
	$scope.idSelectedIncident = null;
	$scope.setSelectedIncident = function (id) {
		$scope.idSelectedIncident = id;
	};

	// when the global manual refresh button is clicked:
	// Fires when $rootScope.$emit('globalRefreshTrigger');
	$rootScope.$on('globalRefreshTrigger',function(event){
		manualRefresh();
	});

	var getData = function(){
		return MonitoringService.getIncidents().then(function(response){
			$scope.incidents = response.incidents;
			$scope.initialLoading = false;
			// update the pagination stuff
			$scope.filtered = filterFilter($scope.incidents, $scope.searchText);
			$scope.totalItems = $scope.filtered.length;
			$scope.noOfPages = Math.ceil($scope.totalItems / $scope.pageSize);
		});
	};

	var startInterval = function(ms) {
			if (angular.isNumber(ms) && ms > 0) {
				dataInterval = $interval(function() {
					getData();
				}, ms);
			}
	};

	var stopInterval = function() {
		if(angular.isDefined(dataInterval)) {
			$interval.cancel(dataInterval);
		}
	};

	//function to reset the refresh timeout (in seconds)
	var updateRefresh = function(refreshMS) {
		// stop/cancel the existing timer
		stopInterval();
		// if LTE zero, we won't use a timer:
		if (angular.isNumber(refreshMS) && refreshMS > 0) {
			startInterval(refreshMS);
		}
	};

  // when the global refresh interval is changed:
	$rootScope.$watch('globalRefreshInterval',function(newValue){
		// this gets called when the state starts, so it's an init, too.
		counterRequestTimeout = newValue;
		updateRefresh(newValue);
	});

	$scope.$on("$destroy",function(){
		stopInterval();
	});

	$scope.removeIncident = function(id){
		MonitoringService.removeIncident(id).then(getIncidents);
	};

	//get the data on load
	getData();

	$scope.closeDetails = function() {
		$log.info('closing');
		$state.go('dashboard.incidents', $state.params, { reload: false });
	};

	$scope.openServer = function(server) {
		$window.open(server.browserurl, '_blank');
	};
}])

.controller('IncidentCtrl', ['$scope', '$timeout', '$log', '$state', 'MonitoringService', 'id', function ($scope, $timeout, $log, $state, MonitoringService, id) {

	// Make the $state available to the template.
	$scope.$state = $state;

	var getIncident = function(id){
		MonitoringService.incident(id).then(function(response){
			$scope.incident = response;
			$log.debug(response);
		});
	};

	$scope.closeDetails = function() {
		$log.info('closing');
		$state.go('dashboard.incidents', $state.params, { reload: false });
	};

	getIncident(id);
}])
;
