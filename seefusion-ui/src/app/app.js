angular.module('app', [
	'ui.router',
	'ui.bootstrap',
	'checklist-model',
	'nvd3ChartDirectives',
	'toggle-switch',
	'templates.app',
	'templates.common',
	// Controller modules
	'dashboard',
	'server',
	'config',
	'monitoring',
	'snapshot',
	'stack',
	'profiling',
	'counters',
	'dos',
	'log',
	'help',
	'about',
	'info',
	'login',
	// Resources interact with the API
	'resources',
	// Services
	'services.httpRequestInterceptor',
	'services.httpResponseEvaluation',
	'services.httpRequestTracker',
	'services.AppAlert',
	'services.ChartSettings',
	'directives'
]);

angular.module('app').config(['$httpProvider', '$stateProvider', '$urlRouterProvider', '$locationProvider', function ($httpProvider, $stateProvider, $urlRouterProvider, $locationProvider) {
	$httpProvider.interceptors.push('httpResponseEvaluation');

	$httpProvider.interceptors.push('httpRequestInterceptor');

	$locationProvider.html5Mode(false);
	$urlRouterProvider.otherwise('/server/active');
}]);

angular.module('app').run(['$rootScope', '$log', '$location', '$state', '$stateParams', 'httpRequestInterceptor', 'UserService', function($rootScope, $log, $location, $state, $stateParams, httpRequestInterceptor, UserService) {

	if(UserService.getSessionToken()){
		$rootScope.authToken = UserService.getSessionToken();
		UserService.setToken(UserService.getSessionToken());
	}

	UserService.login().then(function(response){
		$rootScope.authToken = UserService.getToken();
		$rootScope.isAuthenticated = UserService.isAuthenticated();
	});

	$rootScope.problem = false;
}]);

angular.module('app').filter('startFrom', function () {
	return function (input, start) {
		if (input) {
			start = +start;
			return input.slice(start);
		}
		return [];
	};
});

angular.module('app').filter('formatIfNumber', function () {
	return function (input) {
		if (!isNaN(parseFloat(input)) && isFinite(input)) {
			//return $filter('number')(input);
			return Number(input).toLocaleString();
		}
		return input;
	};
});

angular.module('app').filter('millSecondsToTimeString', function() {
	return function(millseconds) {
        var seconds = Math.floor(millseconds / 1000);
        var h = 3600;
        var m = 60;
        var hours = Math.floor(seconds/h);
        var minutes = Math.floor( (seconds % h)/m );
        var scnds = Math.floor( (seconds % m) );
        var timeString = '';
        if(scnds < 10) {scnds = "0" + scnds;}
        if(minutes < 10) {minutes = "0"+minutes;}
        if(hours < 10) {hours = "0" + hours;}
        timeString = hours +":"+ minutes +":"+scnds;
        if(timeString === '00:00:00') {timeString = millseconds + 'ms';}
        return timeString;
    };
});
angular.module('app').filter('millSecondsToTimerString', function() {
	return function(millseconds) {
        var seconds = Math.floor(millseconds / 1000);
        var h = 3600;
        var m = 60;
        var hours = Math.floor(seconds/h);
        var minutes = Math.floor( (seconds % h)/m );
        var scnds = Math.floor( (seconds % m) );
        var timeString = '';

        if(hours < 1) {hours = "";}
        else if(hours < 10) {hours = "0" + hours;}

		if(hours.length || hours > 0) {hours = hours + ":";}

        if(minutes < 1 && hours < 1) {minutes = "";}
        else if(hours.length && minutes < 10) {minutes = "0"+minutes;}

        if (minutes > 0 || minutes.length) {minutes = minutes +  ":";}

        if(scnds < 1) {scnds = "";}
        else if(scnds > 0 && scnds < 10) {scnds = "0" + scnds;}

        timeString = hours + minutes + scnds;
        if(!timeString.length) {timeString += millseconds + 'ms';}
        return timeString;
    };
});
angular.module('app').filter('millSecondsToReadable', function() {
	return function(millseconds) {
    var seconds = Math.floor(millseconds / 1000);
    var h = 3600;
    var m = 60;
    var hours = Math.floor(seconds/h);
    var minutes = Math.floor( (seconds % h)/m );
    var scnds = Math.floor( (seconds % m) );
    var timeString = '';
		if(hours === 1) {
			timeString = timeString + "1 Hour ";
		}
		else if(hours > 1) {
			timeString = timeString + hours + " Hours ";
		}

		if(minutes === 1) {
			timeString = timeString + "1 Minute ";
		}
		else if(minutes > 1) {
			timeString = timeString + minutes + " Minutes ";
		}

		if(scnds === 1) {
			timeString = timeString + "1 Second ";
		}
		else if(scnds > 1) {
			timeString = timeString + scnds + " Seconds ";
		}

        if(!timeString.length) {timeString += millseconds + ' Ms';}
        return timeString;
    };
});

angular.module('app').controller('AppCtrl',
	['$rootScope', '$scope', '$log', '$state', '$window', '$location', '$timeout', '$interval', '$modal',
		'httpRequestTracker', 'httpRequestInterceptor', 'AppAlert', 'ServerService', 'UserService',
	function($rootScope, $scope, $log, $state, $window, $location, $timeout, $interval, $modal,
		httpRequestTracker, httpRequestInterceptor, AppAlert, ServerService, UserService) {

	$scope.$state = $state;

	$rootScope.$watch('isAuthenticated',function(newValue){
		$scope.isAuthenticated = newValue;
	});

	// the entire app will do timeouts based on the pod timeout
	$scope.$watch('podtimeout',function(newValue){
		$rootScope.globalRefreshInterval = newValue * 1000;
	});


	// watch this in case we get a connection refused error (see httpResponseEvaluation.js)
	$scope.problem = false;

	var loginModalOpen = false;
	var problemModalOpen = false;

	//--------------------Login Modal---------------------------
	var showLogin = function(redirectState){
		if(!loginModalOpen){
			loginModalOpen = true;
			$scope.modalInstance = $modal.open({
				templateUrl: 'login/login-modal.tpl.html',
				controller: 'LoginCtrl'
			});

			$scope.modalInstance.result.then(function() {
				loginModalOpen = false;
				$rootScope.authToken = UserService.getToken();
				$scope.isAuthenticated = UserService.isAuthenticated();
				$scope.credentials = UserService.getCredentials();

				if(!$scope.isAuthenticated){
					showLogin();
					return false;
				}

				if(redirectState){
					$state.go(redirectState, $state.params, { reload: true });
				}
			}, function() {
				// cancelled
			})['finally'](function(){
				// unset modalInstance to prevent double close of modal when $routeChangeStart
				$scope.modalInstance = undefined;
			});
		}
	};

	$scope.logout = function(){
		UserService.logout().then(function(response){
			// Force a refresh on logout.
			window.location = '/';
		});
	};

	// Fires when $rootScope.$emit('showLogin');
	$rootScope.$on('showLogin',function(event,redirectState){
		showLogin(redirectState);
	});


	//---------------------popup--------------------------------
	var popupOpen = false;

	var showPopup = function(message){
		if(!popupOpen && !loginModalOpen){
			popupOpen = true;

			$scope.modalInstance = $modal.open({
				templateUrl: 'popup.tpl.html',
				controller: 'popupCtrl',
				resolve:{
					message: function(){
						return message;
					},
					message1: function(){
						return message;
					}
				}
			});

			$scope.modalInstance.result.then(function() {
				popupOpen = false;
			}, function() {
				// cancelled
				popupOpen = false;
			})['finally'](function(){
				// unset modalInstance to prevent double close of modal when $routeChangeStart
				$scope.modalInstance = undefined;
			});
		}
	};

	// Fires when $rootScope.$emit('popup','your message here');
	$rootScope.$on('popup',function(event,message){
		showPopup(message);
	});

	$scope.showNav = true;
	$scope.toggleNav = function(){
		// Set this in a factory so it is accessble throughout the app.
		$scope.showNav = !$scope.showNav;
		return $scope.showNav;
	};

	// garbage collection function for data header widgets
	$scope.gc = function(){
		ServerService.garbageCollection().then(function(response){
			if (response.success) {
				$rootScope.$emit('globalRefreshTrigger');
				$scope.$emit('popup',response.success);
			}
			else if (response.error) {
				$scope.$emit('popup',response.error);
			}
		});
	};

	//---------------------Data Header Widgets-------------------------------------
		var widgetInterval = null; //the interval for widgets (in charge or auto refresh)
		$scope.podtimeout = 0; //default widget timeout, value set from cookie
		$scope.server = {};

		var getWidgets = function(){
			return ServerService.getInfo().then(function(data){
				$scope.serverName = data.serverName;
				$scope.serverNameTitle = 'Server: ' + data.serverName;
				$scope.serverMetrics = data.server;
				$scope.requestMetrics = data.requests;
				$scope.queryMetrics = data.queries;
			});
		};

		var startInterval = function(sec) {
			if (angular.isNumber(sec) && sec > 0) {
				widgetInterval = $interval(function() {
					getWidgets();
				}, sec * 1000);
			}
		};

		var stopInterval = function() {
			if(angular.isDefined(widgetInterval)) {
				$interval.cancel(widgetInterval);
			}
		};

		var manualRefresh = function() {
			getWidgets();
		};

		//function to set the widget refresh timeout (in seconds)
		var updatePodRefresh = function(refreshSec) {
			$window.sessionStorage.podTimeout = refreshSec;
			$scope.podtimeout =  refreshSec;
			$log.info('updating pod refresh');
			// refresh the timer
			// stop/cancel the existing timer
			stopInterval();
			// if LTE zero, we won't use a timer:
			if (angular.isNumber(refreshSec) && refreshSec > 0) {
				startInterval(refreshSec);
			}
		};

		//same as above, but with cookies.  Cookies were removed in favor of session storage.
		var updatePodRefreshCookie = function(refreshMS) {
			expireDate.setDate(expireDate.getDate() + 30);
			$cookies.put('podTimeout',refreshMS,{'expires': expireDate}); //set the cookie
			$scope.podtimeout =  refreshMS;
			// refresh the timer
			// stop/cancel the existing timer
			stopInterval();
			// if LTE zero, we won't use a timer:
			if (angular.isNumber(refreshMS) && refreshMS > 0) {
				startInterval(refreshMS);
			}
		};

		$scope.setpodrefresh = function(sec) {
			updatePodRefresh(sec);
		};

		$scope.refresh = function() {
			$rootScope.$emit('globalRefreshTrigger'); // for other pages that will piggyback on this refresh action
			manualRefresh();
		};

		//initialize the pod timeout the stored value, or to 10 seconds if not defined:
		if ($window.sessionStorage.podTimeout && angular.isNumber(Number($window.sessionStorage.podTimeout))) {
			$scope.podtimeout = Number($window.sessionStorage.podTimeout);
			startInterval($scope.podtimeout);
		}
		else {
			$log.info('SeeFusion pod timeout saved setting not found; initializing to zero (manual refresh).');
			updatePodRefresh(0);
		}

		// same thing with cookies (cookies were removed in favor of session storage):
		/*
		if (angular.isNumber(Number($cookies.get('podTimeout')))) {
			$scope.podtimeout = Number($cookies.get('podTimeout'));
		}
		else {
			$log.info('SeeFusion pod timeout cookie not found; initializing to zero.');
			updatePodRefreshCookie(0);
		}
		*/

		$scope.$watch('isAuthenticated',function(newValue,oldValue){
			if($scope.isAuthenticated){
				getWidgets();
			}
		});

	// --------------------END Data Header Widgets-----------------

	// To fire off an alert...
	// $scope.$emit('alert','success','Message',[timeout]);
	$scope.$on('alert',function(event,type,message,timeout){
		// Default timeout to 4500. If -1 passed in, don't close the alert.
		timeout = timeout === -1 ? null : 4500;
		AppAlert.add(type,message,timeout);
	});

	// To clear all alerts... (useful on a new login)
	// $scope.$emit('clearalerts');
	$scope.$on('clearalerts',function(event){
		AppAlert.clear();
	});

	$scope.closeAlert = function(alert){
		AppAlert.closeAlert(alert);
	};

	$scope.clearAlerts = AppAlert.clear;

	//--------------------Problem Modal---------------------------
	var showProblem = function(){
		if(!problemModalOpen){
			problemModalOpen = true;
			$scope.modalInstance = $modal.open({
				templateUrl: 'info/connection-modal.tpl.html',
				controller: 'InfoCtrl'
			});

			$scope.modalInstance.result.then(function() {
				problemModalOpen = false;

				if(!$rootScope.problem){
					showProblem();
					return false;
				}
			}, function() {
				// cancelled
			})['finally'](function(){
				// unset modalInstance to prevent double close of modal when $routeChangeStart
				$scope.modalInstance = undefined;
			});
		}
	};

	// Fires when $rootScope.$emit('showProblem');
	$rootScope.$on('showProblem',function(event,redirectState){
		stopInterval(); //stop the pods from updating
		showProblem();
	});

	// ----------------------- HTTP Status Errors ----------------------------
	$rootScope.$on('httpResponseAlert',function(event,response){
		switch(Number(response.status)) {
			case 0:
				if(loginModalOpen) {loginModalOpen=false;$scope.modalInstance.close();}
				$log.warn('Lost connection to SeeFusion');
				$state.go('info.connection');
				break;
		}
	});

	$scope.$on('$stateChangeSuccess', function(event, current, previous, rejection){
		if(current.breadcrumb)	{$rootScope.breadcrumb=current.breadcrumb;}
	});

	$scope.$on('$routeChangeError', function(event, current, previous, rejection){

	});

	$scope.hasPendingRequests = function () {
		return httpRequestTracker.hasPendingRequests();
	};

	var contentHeight = null;
	$scope.$watch('hasPendingRequests()',function(request){
		// If there aren't any pending requests, and there is data loaded
		// into an element with the .results class, resize the sidebar.
		if(!request){
			$timeout(function(){
				// Only set the contentHeight once.
				if(!contentHeight){
					contentHeight = $('.content').css('height');
					$('#sidebar').css('height','+=' + contentHeight);
				}
			},500);
		}
	});
}]);

angular.module('app').controller('popupCtrl', ['$scope', '$log', '$state', '$modalInstance', '$window', 'message', 'message1', function ($scope, $log, $state, $modalInstance, $window, popupMessage, message, message1) {
	$scope.message = message;

	$scope.cancel = function () {
		$modalInstance.dismiss('cancel');
	};
}]);
