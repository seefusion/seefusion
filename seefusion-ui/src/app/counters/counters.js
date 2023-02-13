angular.module('counters', ['security.authorization'])

.config(['$stateProvider', 'securityAuthorizationProvider', function ($stateProvider, securityAuthorizationProvider) {
	$stateProvider.state('counters', {
		url: '/counters',
		breadcrumb:'Counters',
		templateUrl:'counters/counters.tpl.html',
		controller:'CounterCtrl',
		resolve: {
			authenticatedUser: securityAuthorizationProvider.isAuthenticated
		}
	})
	;
}])

.controller('CounterCtrl', ['$rootScope', '$scope','$log','$interval', '$state', 'CounterService', 'filterFilter',  function ($rootScope, $scope, $log, $interval, $state, CounterService, filterFilter) {

	// Make the $state available to the template.
	$scope.$state = $state;

	$scope.counters = [];

	var defaultHistorySize = 3; // 3 seconds
	var historySize = defaultHistorySize; //the duration we pass to getCounters, in seconds
	if ($rootScope.globalRefreshInterval > 0) {
		historySize = Math.round($rootScope.globalRefreshInterval/1000);
	}

	//paginiation init
	$scope.pageSize = 15;
	$scope.currentPage = 1;
	$scope.searchText = '';
	$scope.noOfPages = 1;
	$scope.totalItems = 10;

	// sort init
	$scope.sortType     = 'timestamp'; // set the default sort type
	$scope.sortReverse  = true;  // set the default sort order

	// initialize the timer
	var dataInterval;

	//internal variable, base refresh interval in MS
	var counterRequestTimeout = 0;

	var getData = function() {
		return CounterService.getCounters(historySize).then(function(response){
			$scope.counters.push(response);
			// update the pagination stuff
			$scope.filtered = filterFilter($scope.counters, $scope.searchText);
			$scope.totalItems = $scope.filtered.length;
			$scope.noOfPages = Math.ceil($scope.totalItems / $scope.pageSize);
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

	var clearCounters = function() {
		$scope.counters = [];
		// update the pagination stuff
		$scope.filtered = filterFilter($scope.counters, $scope.searchText);
		$scope.totalItems = $scope.filtered.length;
		$scope.noOfPages = Math.ceil($scope.totalItems / $scope.pageSize);
	};

	// when the global refresh interval is changed:
	$rootScope.$watch('globalRefreshInterval',function(newValue){
		// this gets called when the state starts, so it's an init, too.
		counterRequestTimeout = newValue;
		updateRefresh(newValue);
		historySize = Math.round(newValue/1000);
		if (historySize < 1) {
			historySize = defaultHistorySize;
		}
	});

	// when the global manual refresh button is clicked:
	// Fires when $rootScope.$emit('globalRefreshTrigger');
	$rootScope.$on('globalRefreshTrigger',function(event){
		manualRefresh();
	});
	
	$scope.clear = function() {
		clearCounters();
	};

	$scope.$on("$destroy",function(){
		stopInterval();
	});

	getData();
}]);
