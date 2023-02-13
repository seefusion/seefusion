angular.module('server', ['security.authorization'])

.config(['$stateProvider', 'securityAuthorizationProvider', function ($stateProvider, securityAuthorizationProvider) {
	$stateProvider.state('server', {
		url: '/server',
		templateUrl:'server/server.tpl.html',
		controller:'ServerCtrl',
		resolve: {
			authenticatedUser: securityAuthorizationProvider.isAuthenticated
		}
	})
		.state('server.active',{
			url:'/active',
			breadcrumb:'Server Monitoring: Active Requests',
			templateUrl:'server/active.tpl.html',
			controller:function($scope){

			}
		})
		.state('server.recent',{
			url:'/recent',
			breadcrumb:'Server Monitoring: Recent Requests',
			templateUrl:'server/recent.tpl.html',
			controller:function($scope){

			}
		})
		.state('server.slow',{
			url:'/slow',
			breadcrumb:'Server Monitoring: Slow Requests',
			templateUrl:'server/slow.tpl.html',
			controller:function($scope){

			}
		})
	;
}])

.controller('ServerCtrl', ['$rootScope', '$scope', '$interval', '$log', '$state', '$modal', '$window', '$tooltip', 'HistoryService', 'RequestService', 'DosService', 'ChartSettings', 'UserService', 'ServerService', function ($rootScope, $scope, $interval, $log, $state, $modal, $window, $tooltip, HistoryService, RequestService, DosService, ChartSettings, UserService, ServerService) {

	// Make the $state available to the template.
	$scope.$state = $state;

	$scope.initialLoading = true;
	$scope.canKill = UserService.canKill();

	//intervals (in charge or refreshing data every X seconds)
	var chartRefresh = 60000; //charts refresh every 60 seconds
	var requestInterval;
	var chartsInterval;

	//-------------------- set getClickable --------------------------
	var setServerVars = function(){
		return ServerService.getSeeFusionInfo().then(function(data){
			$scope.clickableURLs = data.clickableURLs;
			$scope.clickableIPURL = data.clickableIPURL;
			$scope.dosEnabled = data.dosEnabled;
			$scope.displayRelativeTimes = data.displayRelativeTimes;
		});
	};
	setServerVars();

	var getRequestPages = function(){
		$scope.busy = true;
		return RequestService.getRequests($scope.currentState).then(function(response){
			$scope.busy = false;
			$scope.initialLoading = false;
			$scope.pages = response;
			return response;
		});
	};

	// Get the pages data. Requires history data
	var getPages = function(){
		$scope.charts.pages = HistoryService.getPages($scope.history);
	};

	// Get the queries data. Requires history data
	var getQueries = function(){
		$scope.charts.queries = HistoryService.getQueries($scope.history);
	};

	// Get the memory data. Requires history data
	var getMemory = function(){
		$scope.charts.memory = HistoryService.getMemory($scope.history);
	};

	// Get the history data
	var getHistory = function(){
		return HistoryService.getHistory().then(function(data){
			$scope.history = data;
			return data;
		});
	};

	// Chained promises to get History => Pages => Queries => Memory.
	// Then set an interval to reload.
	var getCharts = function(){
		getHistory()
			.then(getPages)
			.then(getQueries)
			.then(getMemory);
	};

	var manualRefresh = function() {
		getRequestPages();
		getCharts();
	};

	var startRequestInterval = function(ms) {
			if (angular.isNumber(ms) && ms > 0) {
				// Now set up the interval to refresh pages
				requestInterval = $interval(function() {
					getRequestPages();
				}, ms);
			}
	};

	var stopRequestInterval = function() {
		if(angular.isDefined(requestInterval)) {
			$interval.cancel(requestInterval);
		}
	};

	//function to reset the refresh timeout (in seconds)
	var updateRequestRefresh = function(refreshMS) {
		// stop/cancel the existing timer
		stopRequestInterval();
		// if LTE zero, we won't use a timer:
		if (angular.isNumber(refreshMS) && refreshMS > 0) {
			startRequestInterval(refreshMS);
		}
	};

	// tab display flags
	$scope.detailRequestNumber = -1;
	$scope.sortType     = 'completed'; // set the default sort type
	$scope.sortReverse  = true;  // set the default sort order

	//internal variable, base refresh interval in MS
	var counterRequestTimeout = 0;

	//Initial state
	var state = $state.current.name.split('.');
	var currentState = state[state.length - 1];
	$scope.currentState = currentState;

	var changeTab = function() {
		// Stuff to do every time the state changes
		$scope.pages = []; //clear the pages
		getRequestPages(); //get the new tab's pages using $scope.currentState
	};

	//State Change
	$rootScope.$on('$stateChangeSuccess', function(){
		var state = $state.current.name.split('.');
		var currentState = state[state.length-1];
		$scope.currentState = currentState;
		changeTab();
	});

	//data
	$scope.pages = [];
	$scope.charts = {
		pages:[],
		queries:[],
		memory:[]
	};
	$scope.killingID = 0;

	// when the global refresh interval is changed:
	$rootScope.$watch('globalRefreshInterval',function(newValue){
		// this gets called when the state starts, so it's an init, too.
		counterRequestTimeout = newValue;
		updateRequestRefresh(newValue);
	});

	// when the global manual refresh button is clicked:
	// Fires when $rootScope.$emit('globalRefreshTrigger');
	$rootScope.$on('globalRefreshTrigger',function(event){
		manualRefresh();
	});

	// Format the x axis for charts
	$scope.chartDateFormat = ChartSettings.xAxisFormatDate;

	// Block the IP address
	$scope.addBlock = function(ip) {
		DosService.blockAddress(ip);
	};

	// Redirect to the stack page.
	$scope.getStack = function(page) {
		$state.go('stack.seestack',{'threadname':page.threadName,'requestid':page.pid});
	};

	// Kill the request.
	$scope.kill = function(page) {
		var pageID = page.pid;
		$scope.killingID = pageID;
		RequestService.killRequest(pageID).then(function(response){
			$scope.killingID = 0;
			if(angular.isDefined(response.pages)) {
				$scope.pages = response.pages;
			}
		});
	};

	// open the page url
	$scope.open = function(page) {
		if(page.url && page.url.length) {
			var prefix = "http://";
			if(page.isSecure) {
				prefix = "https://";
			}
			$window.open(prefix + page.url, '_blank');
		}
		else if(page.length) {
			$window.open(page, '_blank');
		}
	};

	//chart interval (what makes it refresh on interval)
	chartsInterval = $interval(function() {
		getCharts();
	}, chartRefresh);

	var stopChartsInterval = function() {
		if(angular.isDefined(chartsInterval)) {
			$interval.cancel(chartsInterval);
		}
	};

	// When a point on a graph is clicked, capture the data
	// [timestamp,y,series:i]
	$scope.$on('elementClick.directive', function(angularEvent, event){
		var timestamp = event.point[0];
		if($scope.modalInstance) {
			$scope.modalInstance.dismiss('modalCancel');
		}
		$state.go('snapshot.seestack',{'timestamp':timestamp});
	});

	var openModal = function(chart,title){
		$scope.modal = {
			title:title,
			chart:chart,
			height: Math.round($window.innerHeight * 0.5)
		};

		$scope.modalInstance = $modal.open({
			templateUrl: 'server/chart-modal2.tpl.html',
			size: 'lg',
			scope: $scope
		});

		$scope.modalInstance.result.then(function() {
			// $scope.$emit('alert','success','The Administrator has been saved.');

		}, function() {
			// cancelled
		})['finally'](function(){
			// unset modalInstance to prevent double close of modal when $routeChangeStart
			$scope.modalInstance = undefined;
		});
	};

	$scope.modalCancel = function () {
		$scope.modalInstance.dismiss('modalCancel');
	};

	$scope.expandChart = function(chart,title){
		openModal(chart,title);
	};

	//initialize request detail stuff
	$scope.lookingRequest = {
		on:false,
		info: {}
	};

	$scope.requestDetails = function(page,tab) {
		$scope.lookingRequest.on = true;
		$scope.lookingRequest.info = page;
		$scope.detailRequestNumber = page.pid; //for the highlighted row in parent listing
		//$location.hash('req' + page.requestNumber);
		//$anchorScroll();
	};

	$scope.closeDetail = function() {
		$scope.lookingRequest.on = false;
		$scope.lookingRequest.info = {};
	};

	$scope.$on("$destroy",function(){
		stopRequestInterval();
		stopChartsInterval();
	});

	//initial gets
	getRequestPages();
	getCharts();

}])
;
