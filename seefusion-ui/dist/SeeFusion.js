/*! SeeFusion - v5 - 2020-07-19
 * http://seefusion.com/
 * Copyright (c) 2005-2023 Daryl Banttari
 */
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


	//-------------------- set isEnterprise cookie --------------------------
	var setEnt = function(){
		return ServerService.getServerInfo().then(function(data){
			$window.sessionStorage.isEnterprise = data.isLicensedEnt;
			$scope.isEnterprise = data.isLicensedEnt;
			$log.info('set enterprise to ' + $scope.isEnterprise);
		});
	};

	if(!angular.isDefined($window.sessionStorage.isEnterprise)) {
		setEnt();
	}
	else {
		$scope.isEnterprise = $window.sessionStorage.isEnterprise;
	}

	// Fires when $rootScope.$emit('refreshLicense');
	$rootScope.$on('refreshLicense',function(event,redirectState){
		setEnt();
	});



	// watch this in case we get a bad license or connection refused error (see httpResponseEvaluation.js)
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
			case 402:
				if(loginModalOpen) {loginModalOpen=false;$scope.modalInstance.close();}
				$log.warn('license problem detected');
				$state.go('info.license');
				break;
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

angular.module('config', ['security.authorization'])

.config(['$stateProvider', 'securityAuthorizationProvider', function ($stateProvider, securityAuthorizationProvider) {
	$stateProvider.state('config', {
		url: '/config',
		breadcrumb:'Server Configuration',
		templateUrl:'config/config.tpl.html',
		controller:'ConfigCtrl',
		resolve: {
			authenticatedUser: securityAuthorizationProvider.isAuthenticated,
			configAccess: securityAuthorizationProvider.canConfig
		}
	})
	;
}])

.controller('ConfigCtrl', ['$rootScope', '$scope', '$timeout', '$log', '$state', 'ConfigService', 'UserService', 'DosService', function ($rootScope, $scope, $timeout, $log, $state, ConfigService, UserService, DosService) {

	// Make the $state available to the template.
	$scope.$state = $state;

	$scope.selectedDriver = {};

	$scope.dbTypes = [{label: 'MySQL: org.gjt.mm.mysql.Driver', dbType:'MySQL', driver: 'org.gjt.mm.mysql.Driver'},
		{label: 'MySQL: com.mysql.jdbc.Driver', dbType:'MySQL', driver: 'com.mysql.jdbc.Driver'},
		{label: 'MSSQLServer: macromedia.jdbc.MacromediaDriver', dbType:'MSSQLServer', driver: 'macromedia.jdbc.MacromediaDriver'},
		{label: 'Sybase: macromedia.jdbc.MacromediaDriver', dbType:'Sybase', driver: 'macromedia.jdbc.MacromediaDriver'},
		{label: 'OracleThin: oracle.jdbc.driver.OracleDriver', dbType:'OracleThin', driver: 'oracle.jdbc.driver.OracleDriver'},
		{label: 'DB2 OS390: macromedia.jdbc.MacromediaDriver', dbType:'DB2 OS390', driver: 'macromedia.jdbc.MacromediaDriver'},
		{label: 'SybaseJConnect: com.sybase.jdbc2.jdbc.SybDriver', dbType:'SybaseJConnect', driver: 'com.sybase.jdbc2.jdbc.SybDriver'},
		{label: 'JTDS: net.sourceforge.jtds.jdbc.Driver', dbType:'JTDS', driver: 'net.sourceforge.jtds.jdbc.Driver'}];
		/*
		{label: "PostgreSQL: org.postgresql.Driver", dbType:'PostgreSQL', driver: "org.postgresql.Driver"}
		{label: "DB2: macromedia.jdbc.MacromediaDriver", dbType:'DB2', driver: "macromedia.jdbc.MacromediaDriver"}
		{label: "MSAccess: macromedia.jdbc.MacromediaDriver", dbType:'MSAccess', driver: "macromedia.jdbc.MacromediaDriver"}
		{label: "Informix: macromedia.jdbc.MacromediaDriver", dbType:'Informix', driver: "macromedia.jdbc.MacromediaDriver"}
		*/

	$scope.tabSelected = function(s) {
		//$log.info('tab clicked - ' + s);
		// s is the tab name.  You can do anything you want here when a tab is clicked.
		if(s !== 'Database Logging') {
			$scope.configsSaved = false; //flag for showing SAVED notification, we reset it when the user switches tabs
		}

	};

	$scope.arrayContains = function(item, array) {
		return (-1 !== array.indexOf(item));
	};

	var getLongest = function (a) {
		var c = 0, d = 0, l = 0, i = a.length;
		if (i) {
			while (i--) {
				d = a[i].length;
				if (d > c) {
					l = i; c = d;
				}
			}
		}
		return a[l];
	};


	var getConfig = function(){
		ConfigService.getConfig().then(function(response){
			$scope.config = response;
			$scope.dbTypeArray = response.driverNames;
			$scope.dbTypeMaxLength = getLongest(response.driverNames).length;
		});
	};

	//initial get config
	if(UserService.canConfig()){
		getConfig();
	} else {
		$rootScope.$emit('showLogin',$state.current.name);
	}

	var refreshLicenseCookie = function() {
		$rootScope.$emit('refreshLicense');
	};

	// Save the config changes and refresh the model.
	$scope.saveConfigOld = function(item){
		ConfigService.save(item).then(function(response){
			$scope.config = response;
		});
	};

	// apply license key from form on bad license page
	var applyKey = function(key){
		var keyObj = {license:key};
		var msg = "";
		ConfigService.applyLicenseKey(keyObj).then(function(response){
			if (response.success) {
				$log.info('New license activation success');
				$rootScope.$emit('refreshLicense'); // refresh the isEnterprise cookie
				if(response.message) {
					msg = response.message;
				}
				else {
					msg = "License has been activated successfully";
				}
			}
			else {
				if(response.message) {
					msg = response.message;
				}
				else {
					msg = "Sorry, the license key you entered did not activate properly";
				}
			}
			$scope.$emit('popup',msg);
		});
	};

	// Save the config changes and refresh the model.
	$scope.saveConfig = function(item,sectionIndex){
		ConfigService.save(item).then(function(response){
			//sectionIndex is the tab that is/was active.  We don't need this anymore but will keep it around

			//if a message was returned, there is a problem, so we'll display it
			if(response.message) {
				$rootScope.$emit('popup',response.message);
			}

			//reset the editing flag of the item edited:
			item.editing = false;

			//refresh license enterprise cookie
			if(item.configitem === 'license') {
				applyKey(item.value);
				$timeout(refreshLicenseCookie, 5000); // wait 5 sec for the license to take
			}
		});
	};

	// Save the config changes in batch (via array), and refresh the model.
	$scope.saveConfigs = function(section,sectionIndex){
		ConfigService.saveItems(section.items).then(function(response){
			// add an isActive property to each section (tab) so the UI can determine which one should be active
			var sec;
			for (sec = 0; sec < response.sections.length; ++sec) {
				response.sections[sec].isActive = false;
			}
			response.sections[sectionIndex].isActive = true;
			$scope.config = response; //overwrite the config values returned by the save method
			$scope.configsSaved = true;
		});
	};

	$scope.createTables = function(){
		$log.info('create tables...');
		ConfigService.createTables().then(function(response){
			$log.info('Not currently working.');
		});
	};

	$scope.testLog = function(what){
		$log.info('testing' + what);
	};


}]);

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

angular.module('dos', ['security.authorization'])

.config(['$stateProvider', 'securityAuthorizationProvider', function ($stateProvider, securityAuthorizationProvider) {
	$stateProvider.state('dos', {
		url: '/dos',
		breadcrumb:'DOS Protection',
		templateUrl:'dos/dos.tpl.html',
		controller:'DosCtrl',
		resolve: {
			authenticatedUser: securityAuthorizationProvider.isAuthenticated,
			configAccess: securityAuthorizationProvider.canConfig
		}
	})
	;
}])

.controller('DosCtrl', ['$rootScope', '$scope', '$timeout', '$log', '$modal', '$state', 'DosService', 'UserService', function ($rootScope, $scope, $timeout, $log, $modal, $state, DosService, UserService) {

	// Make the $state available to the template.
	$scope.$state = $state;

	$scope.protectionOptions = [
	{label:"Temporary",element:"tempban",hint:"Temporary ban the IP address"},
	{label:"Permanent",element:"permban",hint:"Permanently ban the IP address"},
	{label:"Reject",element:"reject",hint:"After the concurrent request limit is hit, automatically ban IP address"}
	];

	$scope.blockLists = [
		{label:"Temporary", element:"tempblocklist", hint:"Temporarily banned IP addresses", isExclude:false, isActive:true},
		{label:"Permanent", element:"permblocklist", hint:"Permanently banned IP addresses", isExclude:false, isActive:false},
		{label:"Exclusions", element:"excludes", hint:"IP addresses that will never get banned", isExclude:true, isActive:false}
	];

	$scope.addingExclusion = false;
	$scope.newExclusion = {ip:''};

	var getConfig = function(){
		DosService.getConfig().then(function(response){
			$scope.dosConfig = response;
		});
	};

	var getStatus = function(){
		DosService.getStatus().then(function(response){
			$scope.dosStatus = response;
		});
	};

	if(UserService.canConfig()){
		getConfig();
		getStatus();
	} else {
		$rootScope.$emit('showLogin',$state.current.name);
	}

	$scope.removeIP = function(listing,ip){
		$log.info(listing);
		if (listing === 'excludes') {
			DosService.removeExclude(ip).then(function(response){
				$scope.dosStatus = response;
			});
		}
		else {
			DosService.removeAddress(ip).then(function(response){
				$scope.dosStatus = response;
			});
		}
	};

	$scope.saveConfig = function(){
		DosService.saveConfig($scope.dosConfig).then(function(response){
			$scope.dosConfig = response;
			$rootScope.$emit('popup','DOS protection settings have been saved');
		});
	};

	$scope.addExclusion = function(){
		$scope.addingExclusion = true;
	};

	$scope.insertExclusion = function(){
		$log.info($scope.newExclusionIP);
		DosService.addExclude($scope.newExclusion.ip).then(function(response){
			$scope.dosStatus = response;
			$rootScope.$emit('popup','IP Address has been added to the exclusion list');
			$scope.closeExclusion();
		});
	};

	$scope.closeExclusion = function(){
		$scope.newExclusion = {ip:''};
		$scope.addingExclusion = false;
	};
}]); 
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
angular.module('info', [])

.config(['$stateProvider', function ($stateProvider) {
	$stateProvider.state('info', {
		url: '/info',
		breadcrumb:'Information',
		templateUrl:'info/info.tpl.html',
		controller:'InfoCtrl',
	})
	.state('info.license',{
			url:'/license',
			breadcrumb:'SeeFusion License Issue',
			templateUrl:'info/license.tpl.html',
			controller:function($scope){

			}
		})
		.state('info.connection',{
			url:'/connection',
			breadcrumb:'Server Connectivity Problem',
			templateUrl:'info/connectivity.tpl.html',
			controller:'ConnectionCtrl'
		})
	;
}])

.controller('InfoCtrl', ['$rootScope', '$scope','$log', '$state', 'ConfigService', function ($rootScope, $scope, $log, $state, ConfigService) {
	// Make the $state available to the template.
	$scope.$state = $state;

	$rootScope.problem = true;

	// apply license key from form on bad license page
	$scope.applyKey = function(key){
		var keyObj = {license:key};
		var msg = "";
		ConfigService.applyLicenseKey(keyObj).then(function(response){
			if (response.success) {
				$log.info('New license activation success');
				$rootScope.$emit('refreshLicense'); // refresh the isEnterprise cookie
				if(response.message) {
					msg = response.message;
				}
				else {
					msg = "License has been activated successfully";
				}
				$rootScope.problem = false;
				$state.go('server'); //go to the home page
			}
			else {
				if(response.message) {
					msg = response.message;
				}
				else {
					msg = "Sorry, the license key you entered did not activate properly";
				}
			}
			$scope.$emit('popup',msg);	
		});
	};

}])

.controller('ConnectionCtrl', ['$rootScope', '$scope','$timeout','$log', '$state', 'ConfigService', function ($rootScope, $scope, $timeout, $log, $state, ConfigService) {
	// Make the $state available to the template.
	$scope.$state = $state;

	$rootScope.problem = true;

	// Test the connection, and if it's okay, send the user to the home page
	var testConnection = function() {
		return ConfigService.about().then(function(){
			window.location.href="/";
		});
	};

	$scope.retry = function() {
		testConnection();
	};

	testConnection();
}])

; 
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
angular.module('login', [])

.controller('LoginCtrl', ['$scope', '$modalInstance', 'UserService', '$timeout', '$rootScope', function ($scope, $modalInstance, UserService, $timeout, $rootScope) {

	$timeout(function () {
	        $('#loginpassword').focus();
	      }, 100);

	$scope.password = null;

	$scope.login = function(){
		if($scope.password){
			UserService.login($scope.password).then(function(isAuthenticated){
				if (isAuthenticated) {
					$modalInstance.close();
					$rootScope.$emit('globalRefreshTrigger');
				}
			});
		}
	};

}]);

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
angular.module('profiling', ['security.authorization'])

.config(['$stateProvider', 'securityAuthorizationProvider', function ($stateProvider, securityAuthorizationProvider) {
	$stateProvider.state('profiling', {
		url: '/profiling',
		breadcrumb:'Server Profiling',
		templateUrl:'profiling/profiling.tpl.html',
		controller:'ProfilingCtrl',
		resolve: {
			authenticatedUser: securityAuthorizationProvider.isAuthenticated
		}
	})
	.state('profile',{
		url:'/profile/:id',
		breadcrumb:'Profile Details',
		templateUrl:'profiling/profile.tpl.html',
		controller:'ProfileCtrl',
		resolve: {
			id:['$stateParams', function($stateParams){
			return $stateParams.id;
		}]
		}
	})
	;
}])

.filter('durationString', function() {
	return function(millseconds) {
        var seconds = Math.floor(millseconds / 1000);
        var h = 3600;
        var m = 60;
        var hours = Math.floor(seconds/h);
        var minutes = Math.floor( (seconds % h)/m );
        var scnds = Math.floor( (seconds % m) );
        var timeString = '';

        if(hours < 1) {hours = "";}
		else if (hours > 1) {hours = hours + " hours";}
		else {hours = hours + " hour";}

		if(minutes < 1) {minutes = "";}
		else if (minutes > 1) {minutes = minutes + " minutes";}
		else {minutes = minutes + " minute";}

		if(scnds < 1) {scnds = "";}
		else if (scnds > 1) {scnds = scnds + " seconds";}
		else {scnds = scnds + " second";}

        timeString = hours + minutes + scnds;
        if(!timeString.length) {timeString += millseconds + ' ms';}
        return timeString;
    };
})

.controller('ProfilingCtrl', ['$scope', '$interval', '$log', '$state', 'ProfileService', 'filterFilter', function ($scope, $interval, $log, $state, ProfileService, filterFilter) {

	// Make the $state available to the template.
	$scope.$state = $state;

	// Set the initial reload to 0 so the page load immediately.
	var rightNow = new Date();

	// initialize the timers
	var activeInterval;
	var listingInterval;

	// set the refresh rates
	var activeTimeoutMs = 1000;
	var listingTimeoutMs = 10000;

	//track if we're recording or not
	var isRecording = false;

	$scope.status = {};
	$scope.active = {};
	$scope.activeTimeRemaining = 0;
	$scope.timeRemainingPercent = 100;
	$scope.newProfile = {};
	$scope.pageSize = 15;
	$scope.currentPage = 1;
	$scope.searchText = '';
	$scope.noOfPages = 1;
	$scope.totalItems = 10;
	$scope.initialLoading = true;
	$scope.starting = false;
	$scope.stopping = false;
	$scope.durationIntervalOptions = []; // the interval options, calculated after duration is selected
	$scope.durationOptions = [0.1667,0.5,1,2,3,4,5,10,15,20,30,45,60];
	$scope.startClicked = false;

	var intervalOptions = [
		{'lower':0,'upper':0.5,'values':[100,250,500,1000]},
		{'lower':0.5,'upper':1,'values':[250,500,1000,5000]},
		{'lower':1,'upper':5,'values':[500,1000,5000,10000]},
		{'lower':5,'upper':10,'values':[5000,10000,30000,60000]},
		{'lower':10,'upper':20,'values':[30000,60000,120000,300000]},
		{'lower':20,'upper':99999,'values':[60000,120000,300000,600000]}
		];

	$scope.getIntervalOptions = function(durationMinutes) {
		angular.forEach(intervalOptions, function(value){
			if(durationMinutes > value.lower && durationMinutes <= value.upper) {
				$scope.durationIntervalOptions = value.values;
			}
		});
	};

	var activeDuties = function(activeObj) {
		if(typeof activeObj !== "undefined") {
			isRecording = true;
			rightNow = new Date();
			$scope.activeTimeRemaining = activeObj.startTick + activeObj.scheduledDurationMs - rightNow.getTime();
			$scope.timePercent = 100 - (100 * $scope.activeTimeRemaining / activeObj.scheduledDurationMs);
			if($scope.activeTimeRemaining < 0) {
				// the profile is still recording, but it shouldn't be. We can stop it manually if SF doesn't do it.
				$log.warn('Active profile duration reached, stopping now');
				isRecording = false;
				ProfileService.stop().then(function(response){
					$scope.status = response.status;
					$scope.active = response.status.active;
				});
			}
		}
		else {
			$scope.activeTimeRemaining = 0;
			$scope.timePercent = 0;
		}
	};

	var getProfileStatus = function(){
		return ProfileService.getStatus().then(function(response){
			if(typeof response.status.active !== "undefined") {
				$scope.active = response.status.active;
				activeDuties(response.status.active);
			}
			$scope.status = response.status;
			// update the pagination stuff
			$scope.filtered = filterFilter($scope.status.saved, $scope.searchText);
			$scope.totalItems = $scope.filtered.length;
			$scope.noOfPages = Math.ceil($scope.totalItems / $scope.pageSize);
		});
	};

	var getActive = function() {
		ProfileService.getActive($scope.newProfile).then(function(response){
			$scope.active = response.active;
			if(typeof response.active !== "undefined") {
				activeDuties(response.active);
			}
			else {
				$scope.activeTimeRemaining = 0;
				$scope.timePercent = 0;
			}
		});
	};

	var toggleInterval = function(whichTimer,turnOn) {
		switch(whichTimer) {
			case "active":
				if(angular.isDefined(activeInterval)) {
					$interval.cancel(activeInterval);
				}
				if(turnOn) {
					activeInterval = $interval(function() { getActive(); }, activeTimeoutMs);
				}
				break;
			case "listing":
				if(angular.isDefined(listingInterval)) {
					$interval.cancel(listingInterval);
				}
				if(turnOn) {
					listingInterval = $interval(function() { getProfileStatus(); }, listingTimeoutMs);
				}
				break;
		}
	};

	$scope.startProfiling = function(){
		$scope.starting = true;
		$scope.startClicked = true;
		ProfileService.start($scope.newProfile).then(function(response){
			$scope.active = response.active;
			toggleInterval("active",true);//start the active profile refresh
			isRecording = true;

			//initialize the time left:
			rightNow = new Date();
			$scope.activeTimeRemaining =  response.active.startTick + response.active.scheduledDurationMs - rightNow.getTime();
			$scope.timePercent = 0;
			$scope.starting = false;
		});
	};

	$scope.stopProfiling = function(){
		$scope.starting = false; //just in case someone clicked super fast
		$scope.stopping = true;
		ProfileService.stop().then(function(response){
			$scope.status = response.status;
			$scope.active = response.status.active;
			toggleInterval("active",false);//stop the active profile refresh
			isRecording = false;
			$scope.stopping = false;
		});
	};

	$scope.removeProfile = function(profile){
		ProfileService.remove(profile.id).then(getProfileStatus);
	};

	var initialSetup = function() {
		return getProfileStatus().then(function(){
			// set the auto update of active recording, if applicable
			if(isRecording) {
				toggleInterval("active",true);
			}

			//set up the auto update of profile listing
			toggleInterval("listing",true);

			// turn off the initialLoading display flag
			$scope.initialLoading = false;
		});
	};

	// perform the initial setup stuff:
	initialSetup();

	// when user leaves, stop the interval
	$scope.$on("$destroy",function(){
		toggleInterval("active",false);
		toggleInterval("listing",false);
	});

	$scope.nn = function(n) {
		if(n < 10) {
			return '0' + n;
		}
		else {
			return n;
		}
	};
	window.nn = $scope.nn;

	$scope.toDateString = function(ds) {
        return new Date(parseInt(ds.substr(6)));
	};

	// $watch search to update pagination
	$scope.$watch('searchText', function (newVal, oldVal) {
		if ($scope.status.saved) {
		$scope.filtered = filterFilter($scope.status.saved, newVal);
		$scope.totalItems = $scope.filtered.length;
		$scope.noOfPages = Math.ceil($scope.totalItems / $scope.pageSize);
		$scope.currentPage = 1;
		}
	}, true);

}])

.controller('ProfileCtrl', ['$scope', '$timeout', '$log', '$state', 'ProfileService', 'id', function ($scope, $timeout, $log, $state, ProfileService, id) {

	// Make the $state available to the template.
	$scope.$state = $state;

	$scope.profileInfo = {};
	$scope.profile = {};
	$scope.profiles = [];

	var getProfile = function(id){
		ProfileService.getProfile(id).then(function(response){
			$scope.profileInfo = response.profile;
			$scope.profiles = response.analysis.analysis;
			if($scope.profiles.length > 0) {
				$scope.stacktrace = $scope.profiles[0];
			}
			$scope.infos = response.analysis.infos;
		});
	};

	getProfile(id);

	$scope.showTrace = function(stacktrace){
		$scope.stacktrace = stacktrace;
	};

	$scope.nn = function(n) {
		if(n < 10) {
			return '0' + n;
		}
		else {
			return n;
		}
	};
	window.nn = $scope.nn;

	$scope.toDurationString = function(durationMs) {
		var ret = '';
		var durationSec = Math.floor(durationMs / 1000);
		// hours
		if(durationSec / 3600 >= 1) {
			hh = Math.floor(durationSec / 3600);
			ret += hh + ":";
			durationSec -= hh*3600;
		}
		// mins
		if(durationSec / 60 >= 1) {
			var mm = Math.floor(durationSec / 60);
			ret += $scope.nn(mm);
			durationSec -= mm*60;
		}
		ret += ':';
		ret += $scope.nn(durationSec);
		return ret;
	};

}]);

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
angular.module('directives.confirm', [])
	.directive('confirm', [function () {
		return {
			priority: 100,
			restrict: 'A',
			link: {
			pre: function (scope, element, attrs) {
				var msg = attrs.confirm || "Are you sure you want to delete?";
	
				element.bind('click', function (event) {
					if (!confirm(msg)) {
						event.stopImmediatePropagation();
						event.preventDefault();
					}
				});
			}
		}
	};
}]);
angular.module('directives', [
	'directives.smartFloat',
	'directives.confirm',
	'directives.sfMetricWidget',
	'directives.splitArray',
	'directives.toBoolean',
	'directives.numericbinding'
	]);


angular.module('directives.modal', []).directive('modal', ['$parse',function($parse) {
  var backdropEl;
  var body = angular.element(document.getElementsByTagName('body')[0]);
  var defaultOpts = {
    backdrop: true,
    escape: true
  };
  return {
    restrict: 'ECA',
    link: function(scope, elm, attrs) {
      var opts = angular.extend(defaultOpts, scope.$eval(attrs.uiOptions || attrs.bsOptions || attrs.options));
      var shownExpr = attrs.modal || attrs.show;
      var setClosed;

      if (attrs.close) {
        setClosed = function() {
          scope.$apply(attrs.close);
        };
      } else {
        setClosed = function() {
          scope.$apply(function() {
            $parse(shownExpr).assign(scope, false);
          });
        };
      }
      elm.addClass('modal');

      if (opts.backdrop && !backdropEl) {
        backdropEl = angular.element('<div class="modal-backdrop"></div>');
        backdropEl.css('display','none');
        body.append(backdropEl);
      }

      function setShown(shown) {
        scope.$apply(function() {
          model.assign(scope, shown);
        });
      }

      function escapeClose(evt) {
        if (evt.which === 27) { setClosed(); }
      }
      function clickClose() {
        setClosed();
      }

      function close() {
        if (opts.escape) { body.unbind('keyup', escapeClose); }
        if (opts.backdrop) {
          backdropEl.css('display', 'none').removeClass('in');
          backdropEl.unbind('click', clickClose);
        }
        elm.css('display', 'none').removeClass('in');
        body.removeClass('modal-open');
      }
      function open() {
        if (opts.escape) { body.bind('keyup', escapeClose); }
        if (opts.backdrop) {
          backdropEl.css('display', 'block').addClass('in');
          backdropEl.bind('click', clickClose);
        }
        elm.css('display', 'block').addClass('in');
        body.addClass('modal-open');
      }

      scope.$watch(shownExpr, function(isShown, oldShown) {
        if (isShown) {
          open();
        } else {
          close();
        }
      });
    }
  };
}]);

angular.module('directives.numericbinding', [])
  .directive('numericbinding', function () {
    return {
        restrict: 'A',
        require: 'ngModel',
        scope: {
            model: '=ngModel',
        },                
       link: function (scope, element, attrs, ngModelCtrl) {
           if (scope.model && typeof scope.model === 'string') {
               scope.model = parseInt(scope.model);
           }                  
        }
    };
});
angular.module('directives.sfMetricWidget', [])

// NOTES
// Incoming metrics should be in the following format:
// abstract[
//  {'label':'Uptime','value':'10'},
//  {'label':'Memory','value':'23%'}
// ],
// details[
//  {'counter':'1s','pageCount':5,'time':25},
//  {'counter':'10s','pageCount':5,'time':25},
//  {'counter':'60s','pageCount':5,'time':25}
// ],
// info[
//  {'item':'Total','value':'250mb'},
//  {'item':'Used','value':'30mb'},



.directive('sfMetricWidget', ['$parse', '$log', function($parse, $log) {
	
	return{
		restrict: 'EA',
		scope: {
			metrics: '=',
			title: '=',
			refreshtime: '=',
			showcontrols: '=',
			gc: '&',
			refresh: '&',
			setpodrefresh: '&'
		},
		templateUrl: 'directives/sfMetricWidget.tpl.html',
		link: function (scope, element, attrs) {

			scope.$watch('metrics',function(metrics){
				//
			});

			scope.details = {show:false};
			scope.showDetails = function(metrics){
				if(metrics.details.length){
					scope.details['show'] = true;
				}
			};

			scope.hideDetails = function(){
				scope.details['show'] = false;
			};

			scope.setRefresh = function(sec) {
				scope.setpodrefresh({ sec:sec });
			};

			scope.refreshOptions = [5,10,30,60];
		}
	};

}]);

var FLOAT_REGEXP = /^\-?\d+((\.|\,)\d+)?$/;
angular.module('directives.smartFloat', [])

.directive('smartFloat', function() {
	return {
		require: 'ngModel',
		link: function(scope, elm, attrs, ctrl) {
			ctrl.$parsers.unshift(function(viewValue) {
				if (FLOAT_REGEXP.test(viewValue)) {
					ctrl.$setValidity('float', true);
					return parseFloat(viewValue.replace(',', '.'));
				} else {
					ctrl.$setValidity('float', false);
					return undefined;
				}
			});
		}
	};
});
angular.module('directives.splitArray', [])
    .directive('splitArray', [function () {
    return {
        restrict: 'A',
        require: 'ngModel',
        link: function(scope, element, attr, ngModel) {

            function unsquish(text) {
                return text.split("\n");
            }

            function squish(array) {                        
                return array.join("\n");
            }

            ngModel.$parsers.push(unsquish);
            ngModel.$formatters.push(squish);
        }
    };
}]);
angular.module('directives.toBoolean', [])
    .directive('toBoolean', [function () {
    return {
        restrict: 'A',
        require: 'ngModel',
        link: function(scope, element, attr, ngModel) {

            function makeBoolean(text) {
                var b = (text === 'true'); 
                return b;
            }

            function orig(text) {return text;}

            ngModel.$parsers.push(makeBoolean);
            ngModel.$formatters.push(orig);
        }
    };
}]);
angular.module('resources.config',[])

.factory('ConfigService', ['$http', '$log', function ($http, $log) {

	var Config = {};

	Config.getConfig = function(){
		var request = $http.get("/json/getconfig");
		return request.then(function(response){
			return response.data;
		});
	};
	
	Config.save = function(item){
		var request = $http.post("/json/saveconfig", item);
		return request.then(function(response){
			return response.data;
		});
	};

	//ticket 58
	Config.saveItems = function(itemArray){
		var request = $http.post("/json/saveconfigs", itemArray);
		return request.then(function(response){
			return response.data;
		});
	};

	//ticket 17
	Config.createTables = function(){
		var request = $http.get("/json/createtables");
		return request.then(function(response){
			$log.log(response);
		});
	};

	Config.about = function(){
		var request = $http.get("/json/about");
		return request.then(function(response){
			return response.data;
		});
	};	

	//ticket 99
	Config.applyLicenseKey = function(license){
		var request = $http.post("/json/license", license);
		return request.then(function(response){
			return response.data;
		});
	};

	return Config;

}]);
angular.module('resources.counters',[])

.factory('CounterService', ['$http', '$log', function ($http, $log) {

	var Counters = {};

	Counters.getCounters = function(interval){
		var request = $http.get('/json/counters?interval='+interval);
		return request.then(function(response){
			return response.data;
		});
	};

	return Counters;

}]);
angular.module('resources.dos',[])

.factory('DosService', ['$http', '$log', function ($http, $log) {

	var DOS = {};

	DOS.getConfig = function(){
		var request = $http.get("/json/dosgetconfig");
		return request.then(function(response){
			$log.debug(response.data);
			return response.data;
		});
	};

	DOS.saveConfig = function(settings){
		var request = $http.post("/json/dossaveconfig", settings);
		return request.then(function(response){
			return response.data;
		});
	};

	DOS.getStatus = function(){
		var request = $http.get("/json/dosgetstatus");
		return request.then(function(response){
			return response.data;
		});
	};

	DOS.blockAddress = function(ip){
		var request = $http.post("/json/dosblockadd",{'ip':ip});
		return request.then(function(response){
			return response.data;
		});
	};	

	DOS.removeAddress = function(ip){
		var request = $http.post("/json/dosblockremove",{'ip':ip});
		return request.then(function(response){
			return response.data;
		});
	};

	DOS.removeExclude = function(ip){
		var request = $http.post("/json/dosexclusionremove",{'ip':ip});
		return request.then(function(response){
			return response.data;
		});
	};

	DOS.addExclude = function(ip){
		var request = $http.post("/json/dosexclusionadd",{'ip':ip});
		return request.then(function(response){
			return response.data;
		});
	};

	return DOS;

}]);
angular.module('resources.history',[])

.factory('HistoryService', ['$http', '$log', function ($http, $log) {

	var History = {};


	History.getHistory = function(){
		var request = $http.get("/json/gethistoryminutes");
		return request.then(function(response){

			var i;
			var history = response.data.history;
			for (i = 0; i < history.length; ++i) {
				
					history[i].x = new Date(history[i].timestamp);
				
			}
			return history;
		});
		
	};

	History.getSnapshot = function(timestamp){
		var request = $http.get("/json/gethistorysnapshot?timestamp=" + timestamp);
		return request.then(function(response){
			var results = {};
			results['seestack'] = response.data.seestack;
			results['snapshot'] = response.data.snapshot;
			return results;
		});
		
	};

	History.getStack = function(threadname,requestid,isActive){
		var request = $http.get("/json/getstack?threadname=" + threadname + "&requestid=" + requestid + "&activeonly=" + isActive);
		return request.then(function(response){
			var results = {};
			results['seestack'] = response.data.seestack;
			results['rawstack'] = response.data.rawstack;
			results['message'] = response.data.message;
			return results;
		});
		
	};

	// Get the pageCount and queryCount
	History.getPages = function(data){
		var history = data; 
		var keys = [];
		var pageCounts = [];
		var queryCounts = [];
			
		history.forEach(function(item){
			var pageCount = [];
			pageCount.push(Number(item.timestamp));
			pageCount.push(Number(item.pageCount));
			pageCounts.push(pageCount);

			var queryCount = [];
			queryCount.push(Number(item.timestamp));
			queryCount.push(Number(item.queryCount));
			queryCounts.push(queryCount);
		});

		var pages = {};
		pages['key'] = 'Pages';
		pages['values'] = pageCounts;

		var queries = {};
		queries['key'] = 'Queries';
		queries['color'] = 'orange';
		queries['values'] = queryCounts;

		keys.push(pages);
		keys.push(queries);

		return keys;
	};

	// Get the pageTime and queryTime
	History.getQueries = function(data){
		var history = data;
		var keys = [];
		var pageTimes = [];
		var queryTimes = [];
		
		history.forEach(function(item){
			var pageTime = [];
			pageTime.push(Number(item.timestamp));
			pageTime.push(Number(item.avgPageTimeMs));
			pageTimes.push(pageTime);

			var queryTime = [];
			queryTime.push(Number(item.timestamp));
			queryTime.push(Number(item.avgQueryTimeMs));
			queryTimes.push(queryTime);
		});

		var pages = {};
		pages['key'] = 'Avg Page Time';
		pages['values'] = pageTimes;

		var queries = {};
		queries['key'] = 'Avg Query Time';
		queries['color'] = 'orange';
		queries['values'] = queryTimes;

		keys.push(pages);
		keys.push(queries);

		return keys;
	};

	// Get the memory details
	History.getMemory = function(data){
		var history = data;
		var keys = [];
		var memoryTotals = [];
		var memoryUsedTotals = [];
		
		history.forEach(function(item){
			var memoryTotal = [];
			memoryTotal.push(Number(item.timestamp));
			memoryTotal.push(Number(item.memoryTotalMiB));
			memoryTotals.push(memoryTotal);

			var memoryUsedTotal = [];
			memoryUsedTotal.push(Number(item.timestamp));
			memoryUsedTotal.push(Number(item.memoryUsedMiB));
			memoryUsedTotals.push(memoryUsedTotal);
		});

		var pages = {};
		pages['key'] = 'Available';
		pages['values'] = memoryTotals;

		var queries = {};
		queries['key'] = 'Used';
		queries['color'] = 'orange';
		queries['values'] = memoryUsedTotals;

		keys.push(pages);
		keys.push(queries);

		return keys;
	};

	return History;
}]);
angular.module('resources.log',[])

.factory('LogService', ['$http', '$log', function ($http, $log) {

	var Log = {};

	Log.getLog = function(){
		var request = $http.get('/json/log');
		return request.then(function(response){
			return response.data.log;
		});
	};

	return Log;

}]);
angular.module('resources.monitoring',[])

.factory('MonitoringService', ['$http', '$log', function ($http, $log) {

	var Monitoring = {};

	Monitoring.getProfile = function(){
		var request = $http.get('/json/getmonitorconfig');
		return request.then(function(response){
			return response.data;
		});
	};

	Monitoring.getIncidents = function(){
		var request = $http.get('/json/incidents');
		return request.then(function(response){
			return response.data;
		});
	};

	Monitoring.incident = function(id){
		var request = $http.get('/json/incident?id=' + id);
		return request.then(function(response){
			return response.data;
		});
	};

	Monitoring.removeIncident = function(id){
		var request = $http.get('/json/deleteincident?id=' + id);
		return request.then(function(response){
			return response.data;
		});
	};

	Monitoring.remove = function(id){
		var request = $http.post('/json/deletemonitor', {"id":id});
		return request.then(function(response){
			return response.data;
		});
	};

	Monitoring.save = function(rule){
		var request = $http.post('/json/savemonitor', rule);
		return request.then(function(response){
			return response.data;
		});
	};

	Monitoring.rule = {
		isEnabled: true,
		name: '',
		triggerType: '',
		triggerLimit: '',
		excludeURLs: [],
		delay: '',
		isLogResponse: false,
		isKillResponse: false,
		sleep: '',
		isNotifyResponse: false,
		smtpFrom: '',
		smtpTo: '',
		smtpSubject: '',
		notifyDisableMin: ''
	};

	return Monitoring;

}]);

angular.module('resources.profile',[])

.factory('ProfileService', ['$http', '$log', function ($http, $log) {

	var Profile = {};

	Profile.getStatus = function(){
		var request = $http.get('/json/getprofilerstatus');
		return request.then(function(response){
			return response.data;
		});
	};

	Profile.getActive = function(){
		var request = $http.get('/json/getactiveprofile');
		return request.then(function(response){
			return response.data;
		});
	};	

	Profile.start = function(profile){
		// convert scheduled duration from minutes to ms:
		profile.scheduledDurationMs = profile.scheduledDurationMin * 60 * 1000;
		// start profile
		var request = $http.post('/json/profilerstart', profile);
		return request.then(function(response){
			return response.data;
		});
	};

	Profile.stop = function(){
		var request = $http.get('/json/profilerstop');
		return request.then(function(response){
			return response.data;
		});
	};

	Profile.getProfile = function(id){
		var request = $http.get('/json/getprofile?id=' + id);
		return request.then(function(response){
			return response.data;
		});
	};

	Profile.remove = function(id){
		var request = $http.get('/json/deleteprofile?id=' + id);
		return request.then(function(response){
			return response.data;
		});
	};

	return Profile;

}]);
angular.module('resources.requests',[])

.factory('RequestService', ['$http', '$log', function ($http, $log) {

	var Requests = {};

	Requests.getRequests = function(type){
		var request = $http.get("/json/getrequests?type=" + type);
		return request.then(function(response){
			return response.data.pages;
		});
		
	};

	Requests.killRequest = function(pageID){
		var request = $http.post("/json/kill",{'pid':pageID});
		return request.then(function(response){
			return response.data;
		});
	};

	return Requests;

}]);
angular.module('resources', [
	'resources.history',
	'resources.requests',
	'resources.dos',
	'resources.servers',
	'resources.config',
	'resources.counters',
	'resources.log',
	'resources.monitoring',
	'resources.profile'
]);

angular.module('resources.servers',[])

.factory('ServerService', ['$http', '$log', function ($http, $log) {

	var Servers = {};

	Servers.getServers = function(mode){
		if (mode.toUpperCase() !== "INCIDENTS") {
			var request = $http.get("/json/getdashboardservers?mode=" + mode);
			return request.then(function(response){
				var servers = response.data.servers;
				return servers;
			});
		}
		else {
			var servers = {};
			return servers;
		}
	};

	Servers.garbageCollection = function(){
		var request = $http.get("/json/gc");
		return request.then(function(response){
			return response.data;
		});
	};

	Servers.getSeeFusionInfo = function(){
		var request = $http.get("/json/serverinfo");
		return request.then(function(response){
			return response.data.seefusion;
		});
	};

	Servers.getServerInfo = function(){
		var request = $http.get("/json/serverinfo");
		return request.then(function(response){
			return response.data.server;
		});
	};

	Servers.getInfo = function(){
		var request = $http.get("/json/serverinfo");
		return request.then(function(response){
			var data = response.data;
			var metrics = {};
			var color = "info";
			var avgTime = 0;
			var i = 0;
			var counters = "";
			var tempMem = 0;
			var memString = '';

			// BEGIN - Server widget data
				var server = {};
				server['abstract'] = [];
				server['details'] = [];
				server['info'] = [];

				var memory = Math.round((data.server.memory.used/data.server.memory.total) * 100);
				var lastUpdated = new Date(data.timestamp);

				var d = new Date();
				var sameDay = (d.toDateString() === lastUpdated.toDateString());

				memory = memory.toString() + '%';
				server['abstract'].push({label:'Memory',value:memory});
				server['abstract'].push({label:'Uptime',value:data.server.uptime});
				server['abstract'].push({label:'Updated',value:lastUpdated,sameDay:sameDay});

				// add total/used/free memory in info, displays as name/value in the widget
				counters = ["total","used","free"];
				for (i = 0; i < counters.length; i++) {
					tempMem = Math.ceil(data.server.memory[counters[i]] / 1000000);
					if(tempMem > 1000) {
						memString = (tempMem / 1000).toFixed(2).toString() + " GB";
					}
					else {
						memString = tempMem.toString() + " MB"; 
					}
					server['info'].push(
						{
							item:counters[i],
							value:memString, 
							color:"info"
						}
					);
				}

				metrics['server'] = server;
				metrics['serverName'] = data.server.name;
			// END - Server widget data


			// BEGIN - Requests widget data
				var requests = {};
				requests['abstract'] = [];
				requests['details'] = [];	

				requests['abstract'].push({label:'Active',value:data.server.currentRequests });
				requests['abstract'].push({label:'Total',value:data.seefusion.totalPages });

				metrics['requests'] = requests;
			// END - Requests widget data

			

			// BEGIN - Queries widget data
				var queries = {};
				queries['abstract'] = [];
				queries['details'] = [];

				queries['abstract'].push({label:'Active',value:data.server.currentQueries });
				queries['abstract'].push({label:'Total',value:data.seefusion.totalQueries });

				metrics['queries'] = queries;	
			// END - Queries widget data



			// BEGIN - Populate details arrays with counters info
				// We have counters at 1s, 10s, and 60s
				counters = [1,10,60];

				for (i = 0; i < counters.length; i++) {
					//set the color of the text based on the threshold
					color="success"; //green, default
					avgTime = data['counters' + counters[i]].avgPageTimeMs;
					if (avgTime >= data.seefusion.slowPageThreshold) {color = "danger";} //red
					else if (avgTime >= (data.seefusion.slowPageThreshold * 0.8)) {color = "warning";} //yellow

					requests['details'].push(
						{
							counter:counters[i] + 's',
							pageCount:data['counters' + counters[i]].pageCount,
							time:data['counters' + counters[i]].avgPageTimeMs,
							color:color
						}
					);
					//set the color of the text based on the threshold
					color="success"; //green, default
					avgTime = data['counters' + counters[i]].avgQueryTimeMs;
					if (avgTime >= data.seefusion.slowQueryThreshold) {color = "danger";} //red
					else if (avgTime >= (data.seefusion.slowQueryThreshold * 0.8)) {color = "warning";} //yellow

					queries['details'].push(
						{
							counter:counters[i] + 's',
							pageCount:data['counters' + counters[i]].queryCount,
							time:data['counters' + counters[i]].avgQueryTimeMs,
							color:color
						}
					);
				}
 
			// END - Populate details arrays with counters info

			return metrics;
		});
	};

	return Servers;

}]);
angular.module('security.authorization', ['security.user'])

// This service provides guard methods to support AngularJS routes.
// You can add them as resolves to routes to require authorization levels
// before allowing a route change to complete
.provider('securityAuthorization', {

	isAuthenticated: ['securityAuthorization', function(securityAuthorization){
		return securityAuthorization.isAuthenticated();
	}],

	canConfig: ['securityAuthorization', function(securityAuthorization){
		return securityAuthorization.canConfig();
	}],	

	canKill: ['securityAuthorization', function(securityAuthorization){
		return securityAuthorization.canKill();
	}],

	$get: ['UserService', function(UserService) {
		var service = {

			isAuthenticated: function(){
				return UserService.isAuthenticated();
			},

			canConfig: function(){
				return UserService.canConfig();
			},

			canKill: function(){
				return UserService.canKill();
			}


	};

	return service;
	}]
});
angular.module('security', [
	'security.user',
	'security.authorization'
]);
// Based loosely around work by Witold Szczerba - https://github.com/witoldsz/angular-http-auth

// Add this to all http requests.
// X-SeeFusion-Auth

angular.module('security.user', [])

.factory('UserService', ['$http', '$rootScope', '$q', '$log', '$location', '$state', '$window', function($http, $rootScope, $q, $log, $location, $state, $window) {

	// Redirect to the given url (defaults to '/')
	function redirect(url) {
		url = url || '/';
		$location.path(url);
	}

	function storeSession(token){
		$window.sessionStorage.token = token;
	}

	function deleteSession(){
		delete $window.sessionStorage.token;
	}

	var credentials = {
		canAuthenticate: false,
		canConfig: false,
		canKill: false
	};

	var setCredentials = function(auth,config,kill){
		credentials.canAuthenticate = auth;
		credentials.canConfig = config;
		credentials.canKill = kill;
	};

	var authToken = null;

	var authenticated = true;


	// The public API of the service
	var service = {

		login: function(password){
			var request;

			if(password){
				request = $http.post("json/auth", {password:password});
			} else {
				request = $http.post("json/auth", {password:null});
			}

			return request.then(function(response){

				setCredentials(
					response.data.seefusion.canRead,
					response.data.seefusion.canConfig,
					response.data.seefusion.canKill
				);

				if(response.data.auth){
					authToken = response.data.auth;
					storeSession(authToken);
					isAuthenticated = true;
				} else {
					$log.warn('Could not authenticate.');
					$rootScope.$emit('showLogin');
					isAuthenticated = false;
				}
				return isAuthenticated;
			});

		},

		logout: function(){
			credentials = null;
			var request = $http.get("/json/logout");
			return request.then(function(response){
				deleteSession();
				$log.info(response);
			});
		},

		isAuthenticated: function(){
			return authenticated;
		},

		getCredentials: function(){
			return credentials;
		},

		canConfig: function(){
			return credentials.canConfig;
		},

		canKill: function(){
			return credentials.canKill;
		},

		getToken: function(){
			return authToken;
		},

		setToken: function(token){
			authToken = token;
		},

		getSessionToken: function(){
			return $window.sessionStorage.token;
		}

	};

	return service;
}]);

angular.module('services.AppAlert', [])

.factory('AppAlert', ['$rootScope', '$timeout', function($rootScope, $timeout) {
		
		var alertService;
		$rootScope.alerts = [];
		
		return alertService = {
			add: function(type, msg, timeout) {
				$rootScope.alerts.push({
					type: type,
					msg: msg,
					close: function() {
						return this.closeAlert(this);
					}
				});

				if(timeout){
					$timeout(function(){
						alertService.closeAlert(this);
					}, timeout);
				}
			},
			closeAlert: function(alert) {
				return this.closeAlertIdx($rootScope.alerts.indexOf(alert));
			},
			closeAlertIdx: function(index) {
				return $rootScope.alerts.splice(index, 1);
			},
			clear: function(){
				$rootScope.alerts = [];
			}
		};
	}
]);
angular.module('services.ChartSettings', [])

.factory('ChartSettings', ['$timeout', function($timeout) {
		
		var service = {

			xAxisFormatDate: function(){
				return function(d){
					return d3.time.format('%H:%M')(new Date(d));
				};
			}		
		};

		return service;

	}
]);
angular.module('services.exceptionHandler', ['services.i18nNotifications']);

angular.module('services.exceptionHandler').factory('exceptionHandlerFactory', ['$injector', function($injector) {
  return function($delegate) {

    return function (exception, cause) {
      // Lazy load notifications to get around circular dependency
      //Circular dependency: $rootScope <- notifications <- i18nNotifications <- $exceptionHandler
      var i18nNotifications = $injector.get('i18nNotifications');

      // Pass through to original handler
      $delegate(exception, cause);

      // Push a notification error
      i18nNotifications.pushForCurrentRoute('error.fatal', 'error', {}, {
        exception:exception,
        cause:cause
      });
    };
  };
}]);

angular.module('services.exceptionHandler').config(['$provide', function($provide) {
  $provide.decorator('$exceptionHandler', ['$delegate', 'exceptionHandlerFactory', function ($delegate, exceptionHandlerFactory) {
    return exceptionHandlerFactory($delegate);
  }]);
}]);

angular.module('services.httpRequestInterceptor',[])
.factory('httpRequestInterceptor', ['$rootScope', function($rootScope) {

	return {
		request: function ($config){
			$config.headers = {'X-SeeFusion-Auth':$rootScope.authToken};
			return $config;
		}
	};
}]);
angular.module('services.httpRequestTracker', []);
angular.module('services.httpRequestTracker').factory('httpRequestTracker', ['$http', function($http){

  var httpRequestTracker = {};
  httpRequestTracker.hasPendingRequests = function() {
    return $http.pendingRequests.length > 0;
  };

  return httpRequestTracker;
}]);
angular.module('services.httpResponseEvaluation',[])
.factory('httpResponseEvaluation', ['$rootScope', '$q', '$log', function($rootScope, $q, $log) {
	var responseStatus = {
		response: function(response){
			if(response.headers()['content-type'] === "application/json;charset=UTF-8"){

				var data = response.data;
				var errors = response.data.errors;

				// If data doesn't exist reject.				
				if(!data){
					return $q.reject(response);
				}

				// If the API returns success = false, reject.
				if(!data.success){
					return $q.reject(errors);	
				}
			}
			return response;
		},
		responseError: function (response) {
			$log.warn(response);
			//$rootScope.problem={isProblem:true,status:response.status};
			$rootScope.$emit('httpResponseAlert',response);
			return $q.reject(response);
		}
	};

	return responseStatus;
}]);




angular.module('services.i18nNotifications', ['services.notifications', 'services.localizedMessages']);
angular.module('services.i18nNotifications').factory('i18nNotifications', ['localizedMessages', 'notifications', function (localizedMessages, notifications) {

  var prepareNotification = function(msgKey, type, interpolateParams, otherProperties) {
     return angular.extend({
       message: localizedMessages.get(msgKey, interpolateParams),
       type: type
     }, otherProperties);
  };

  var I18nNotifications = {
    pushSticky:function (msgKey, type, interpolateParams, otherProperties) {
      return notifications.pushSticky(prepareNotification(msgKey, type, interpolateParams, otherProperties));
    },
    pushFleeting:function (msgKey, type, interpolateParams, otherProperties) {
      return notifications.pushFleeting(prepareNotification(msgKey, type, interpolateParams, otherProperties));
    },
    pushForCurrentRoute:function (msgKey, type, interpolateParams, otherProperties) {
      return notifications.pushForCurrentRoute(prepareNotification(msgKey, type, interpolateParams, otherProperties));
    },
    pushForNextRoute:function (msgKey, type, interpolateParams, otherProperties) {
      return notifications.pushForNextRoute(prepareNotification(msgKey, type, interpolateParams, otherProperties));
    },
    getCurrent:function () {
      return notifications.getCurrent();
    },
    remove:function (notification) {
      return notifications.remove(notification);
    },
    removeAll:function () {
      return notifications.removeAll();
    }
  };

  return I18nNotifications;
}]);
angular.module('services.localizedMessages', []).factory('localizedMessages', ['$interpolate', 'I18N.MESSAGES', function ($interpolate, i18nmessages) {

  var handleNotFound = function (msg, msgKey) {
    return msg || msgKey;
  };

  return {
    get : function (msgKey, interpolateParams) {
      var msg =  i18nmessages[msgKey];
      if (msg) {
        return $interpolate(msg)(interpolateParams);
      } else {
        return handleNotFound(msg, msgKey);
      }
    }
  };
}]);
angular.module("templates.app", ["about/about.tpl.html", "config/config.tpl.html", "counters/counters.tpl.html", "dashboard/dashboard.tpl.html", "dashboard/incident.tpl.html", "dashboard/incidents.tpl.html", "dashboard/servers.tpl.html", "dos/dos.tpl.html", "dos/list.tpl.html", "dos/settings.tpl.html", "header.tpl.html", "help/help.tpl.html", "info/connection-modal.tpl.html", "info/connectivity.tpl.html", "info/info.tpl.html", "info/license.tpl.html", "log/log.tpl.html", "login/login-modal.tpl.html", "monitoring/edit.tpl.html", "monitoring/incidents.tpl.html", "monitoring/monitoring.tpl.html", "monitoring/rules.tpl.html", "nav.tpl.html", "navExpand.tpl.html", "notifications.tpl.html", "popup.tpl.html", "problemHeader.tpl.html", "profiling/profile.tpl.html", "profiling/profiling.tpl.html", "server/active.tpl.html", "server/chart-modal.tpl.html", "server/chart-modal2.tpl.html", "server/recent.tpl.html", "server/requestDetails.tpl.html", "server/server.tpl.html", "server/slow.tpl.html", "sidebar.tpl.html", "snapshot/seestack.tpl.html", "snapshot/snapshot.tpl.html", "snapshot/trace.tpl.html", "stack/seestack.tpl.html", "stack/stack.tpl.html", "stack/trace.tpl.html"]);

angular.module("about/about.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("about/about.tpl.html",
    "<h1>About</h1>\n" +
    "\n" +
    "<img src=\"images/loading.gif\" ng-show=\"busy\">\n" +
    "<p ng-hide=\"busy\">\n" +
    "Version {{about.version}} build {{about.buildNumber}}\n" +
    "</p>\n" +
    "<p class=\"sf-copyright push-down\">\n" +
    "	<div>SeeFusion copyright &copy; {{about.year}} Daryl Banttari Consulting</div>\n" +
    "	<div><a href=\"http://angularjs.org\" target=\"_blank\">AngularJS</a> copyright &copy; 2013 Google, used under the MIT license.</div>\n" +
    "	<div><a href=\"http://jquery.org\" target=\"_blank\">JQuery</a> copyright &copy; 2013 The JQuery Foundation, used under the MIT license.</div>\n" +
    "	<div><a href=\"http://angular-ui.github.io/bootstrap/\" target=\"_blank\">AngularUI</a> copyright &copy; 2012 - 2014 the AngularUI Team, used under the MIT license.</div>\n" +
    "	<div><a href=\"http://d3js.org\" target=\"_blank\">D3</a> copyright &copy; 2012 Michael Bostock, used under the BSD license.</div>\n" +
    "	<div><a href=\"https://github.com/cmaurer/angularjs-nvd3-directives\" target=\"_blank\">angularjs-nvd3-directives</a> copyright &copy; 2013 n3-charts, used under the Apache license.</div>\n" +
    "	<div><a href=\"http://getbootstrap.com\" target=\"_blank\">Twitter Bootstrap</a> copyright &copy; 2013 Twitter, used under the MIT license.</div>\n" +
    "</p>");
}]);

angular.module("config/config.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("config/config.tpl.html",
    "<!-- <div>\n" +
    "	<h3>You must be logged in with Config authorization to use this page.</h3>\n" +
    "</div> -->\n" +
    "<div class=\"content\">\n" +
    "	<tabset class=\"config\">\n" +
    "		<tab ng-repeat=\"section in config.sections\" heading=\"{{section.title}}\" data-spy=\"scroll\" data-target=\"#confignav\" active=\"section.isActive\" select=\"tabSelected(section.title)\">\n" +
    "			<a name=\"{{section.title}}\"></a>\n" +
    "\n" +
    "			<div class=\"table-responsive\" ng-hide=\"section.title === 'Database Logging'\">\n" +
    "			<table class=\"table table-striped table-hover\">\n" +
    "			<tbody>\n" +
    "				<tr ng-repeat=\"item in section.items\">\n" +
    "					<td class=\"col-sm-2\"><strong>{{item.configitem}}</strong></td>\n" +
    "					<td class=\"col-sm-7\" ng-switch=\"item.type\">\n" +
    "						<span ng-switch-when=\"booleantrue\" class=\"btn-group\">\n" +
    "							<toggle-switch class=\"switch-success\" ng-model=\"item.value\" on-label=\"Enabled\" off-label=\"Disabled\" ng-value=\"item.value\" on-change=\"saveConfig(item,$parent.$parent.$index)\"></toggle-switch>\n" +
    "						</span>\n" +
    "						<span ng-switch-when=\"booleanfalse\" class=\"btn-group\">\n" +
    "							<toggle-switch class=\"switch-success\" ng-model=\"item.value\" on-label=\"Enabled\" off-label=\"Disabled\" ng-value=\"item.value\" on-change=\"saveConfig(item,$parent.$parent.$index)\"></toggle-switch>\n" +
    "						</span>\n" +
    "						<span ng-switch-when=\"boolean\" class=\"btn-group\">\n" +
    "							<toggle-switch class=\"switch-success\" ng-model=\"item.value\" on-label=\"Enabled\" off-label=\"Disabled\" ng-value=\"item.value\" on-change=\"saveConfig(item,$parent.$parent.$index)\"></toggle-switch>\n" +
    "						</span>\n" +
    "						<span ng-switch-when=\"text\" class=\"input-append\" ng-click=\"item.editing=true\">\n" +
    "							<input name=\"item{{$index}}\" type=\"text\" class=\"input-xxlarge fatty\" ng-model=\"item.value\" id=\"item{{$index}}\" ng-disabled=\"!item.editing\">\n" +
    "							<button class=\"btn btn-sm btn-default\" type=\"button\" ng-hide=\"item.editing\" ng-click=\"item.editing=true\">Edit</button>\n" +
    "							<button class=\"btn btn-sm btn-primary\" type=\"button\" ng-click=\"saveConfig(item,$parent.$parent.$index)\" ng-show=\"item.editing\">Save</button>\n" +
    "						</span>\n" +
    "						<span ng-switch-when=\"password\" class=\"input-append\" ng-click=\"item.editing=true\">\n" +
    "							<input name=\"item{{$index}}\" type=\"password\" class=\"input-xxlarge fatty\" ng-model=\"item.value\" id=\"item{{$index}}\" ng-disabled=\"!item.editing\">\n" +
    "							<button class=\"btn btn-sm btn-default\" type=\"button\" ng-hide=\"item.editing\" ng-click=\"item.editing=true\">Edit</button>\n" +
    "							<button class=\"btn btn-sm btn-primary\" type=\"button\" ng-click=\"saveConfig(item,$parent.$parent.$index)\" ng-show=\"item.editing\">Save</button>\n" +
    "						</span>\n" +
    "						<span ng-switch-when=\"integer\" class=\"input-append\" ng-click=\"item.editing=true\">\n" +
    "							<input name=\"item{{$index}}\" numericbinding type=\"number\" class=\"input-mini\" ng-model=\"item.value\" id=\"item{{$index}}\" min=\"0\" ng-disabled=\"!item.editing\" >\n" +
    "							<button class=\"btn btn-sm btn-default\" type=\"button\" ng-hide=\"item.editing\" ng-click=\"item.editing=true\">Edit</button>\n" +
    "							<button class=\"btn btn-sm btn-primary\" type=\"button\" ng-click=\"saveConfig(item,$parent.$parent.$index)\" ng-show=\"item.editing\">Save</button>\n" +
    "						</span>\n" +
    "						<span ng-switch-when=\"longtext\" class=\"input-append\" ng-click=\"item.editing=true\">\n" +
    "							<textarea name=\"item{{$index}}\" ng-model=\"item.value\" class=\"input-xxlarge fatty\" rows=5 id=\"item{{$index}}\" ng-disabled=\"!item.editing\"></textarea>\n" +
    "							<button class=\"btn btn-sm btn-default\" type=\"button\" ng-hide=\"item.editing\" ng-click=\"item.editing=true\">Edit</button>\n" +
    "							<button class=\"btn btn-sm btn-primary\" type=\"button\" ng-click=\"saveConfig(item,$parent.$parent.$index)\" ng-show=\"item.editing\">Save</button>\n" +
    "						</span>\n" +
    "						<span ng-switch-when=\"license\" class=\"input-append\" ng-click=\"item.editing=true\">\n" +
    "							<textarea name=\"item{{$index}}\" ng-model=\"item.value\" class=\"input-xxlarge fatty\" rows=5 id=\"item{{$index}}\" ng-disabled=\"!item.editing\"></textarea>\n" +
    "							<button class=\"btn btn-sm btn-default\" type=\"button\" ng-hide=\"item.editing\" ng-click=\"item.editing=true\">Edit</button>\n" +
    "							<button class=\"btn btn-sm btn-primary\" type=\"button\" ng-click=\"saveConfig(item,$parent.$parent.$index)\" ng-show=\"item.editing\">Save</button>\n" +
    "						</span>\n" +
    "						<span ng-switch-when=\"select\" class=\"input-append\" ng-click=\"item.editing=true\">\n" +
    "							<select name=\"item{{$index}}\" ng-model=\"item.value\" ng-options=\"value for value in item.options\" id=\"item{{$index}}\" ng-disabled=\"!item.editing\"></select>\n" +
    "							<button class=\"btn btn-sm btn-primary\" type=\"button\" ng-click=\"saveConfig(item,$parent.$parent.$index)\" ng-show=\"item.editing\">Save</button>\n" +
    "						</span>\n" +
    "						<span ng-switch-when=\"createtables\">\n" +
    "							<button type=\"button\" class=\"btn btn-sm btn-primary\" value=\"true\" ng-click=\"createTables()\">&nbsp;Create Tables&nbsp;</button>\n" +
    "						</span>\n" +
    "						<span ng-switch-when=\"dbdriver\" class=\"input-append\" ng-click=\"item.editing=true\">\n" +
    "							<select name=\"item{{$index}}\" ng-model=\"item.value\" ng-options=\"value.driver as value.label for value in dbTypes\" id=\"item{{$index}}\" ng-disabled=\"!item.editing\"></select>\n" +
    "							<button class=\"btn btn-sm btn-default\" type=\"button\" ng-hide=\"item.editing\" ng-click=\"item.editing=true\">Edit</button>\n" +
    "							<button class=\"btn btn-sm btn-primary\" type=\"button\" ng-click=\"saveConfig(item,$parent.$parent.$index)\" ng-show=\"item.editing\">Save</button>\n" +
    "						</span>\n" +
    "\n" +
    "						<span ng-switch-default>\n" +
    "							<pre>{{item|json}}</pre>\n" +
    "						</span>\n" +
    "						<span>\n" +
    "							<img src=\"images/loading.gif\" ng-show=\"item.busy\" />\n" +
    "							<img src=\"images/messages/error.png\" ng-show=\"item.error\" />\n" +
    "							<img src=\"images/messages/success.png\" ng-show=\"item.saved\" />\n" +
    "							<span class=\"error-message\">{{item.errorMessage}}</span>\n" +
    "						</span>\n" +
    "					</td>\n" +
    "					<td class=\"col-sm-3\">{{item.description}}</td>\n" +
    "				</tr>\n" +
    "			</tbody>\n" +
    "			</table>\n" +
    "		</div>	\n" +
    "\n" +
    "		<div ng-show=\"section.title === 'Database Logging'\">\n" +
    "			<!-- display this as a multi field form, rather than each item having its own save button -->\n" +
    "			<form name=\"dbSettingsForm\" novalidate>\n" +
    "			<table class=\"table table-striped table-hover\">\n" +
    "			<tbody>				 \n" +
    "				<tr ng-repeat=\"item in section.items\">\n" +
    "					<td class=\"col-sm-2\"><label for=\"item{{$index}}\"><strong>{{item.configitem}}</strong></label></td>\n" +
    "					<td class=\"col-sm-7\" ng-switch=\"item.type\">\n" +
    "						<span ng-switch-when=\"booleantrue\" class=\"btn-group\">\n" +
    "							<toggle-switch class=\"switch-success\" ng-model=\"item.value\" on-label=\"Enabled\" off-label=\"Disabled\" ng-value=\"item.value\"></toggle-switch>\n" +
    "						</span>\n" +
    "\n" +
    "						<span ng-switch-when=\"boolean\" class=\"btn-group\">\n" +
    "							<toggle-switch class=\"switch-success\" ng-model=\"item.value\" on-label=\"Enabled\" off-label=\"Disabled\" ng-value=\"item.value\"></toggle-switch>\n" +
    "						</span>\n" +
    "						\n" +
    "						<span ng-switch-when=\"text\" class=\"input-append\">\n" +
    "							<input name=\"item{{$index}}\" type=\"text\" class=\"input-xxlarge fatty\" ng-model=\"item.value\" id=\"item{{$index}}\">\n" +
    "						</span>\n" +
    "						<span ng-switch-when=\"password\" class=\"input-append\">\n" +
    "							<input name=\"item{{$index}}\" type=\"password\" class=\"input-xxlarge fatty\" ng-model=\"item.value\" id=\"item{{$index}}\">\n" +
    "						</span>\n" +
    "						<span ng-switch-when=\"integer\" class=\"input-append\">\n" +
    "							<input name=\"item{{$index}}\" numericbinding type=\"number\" class=\"input-mini\" ng-model=\"item.value\" id=\"item{{$index}}\">\n" +
    "						</span>\n" +
    "						<span ng-switch-when=\"longtext\" class=\"input-append\">\n" +
    "							<textarea name=\"item{{$index}}\" ng-model=\"item.value\" class=\"input-xxlarge fatty\" rows=5 id=\"item{{$index}}\"></textarea>\n" +
    "						</span>\n" +
    "						<span ng-switch-when=\"license\" class=\"input-append\">\n" +
    "							<textarea name=\"item{{$index}}\" ng-model=\"item.value\" class=\"input-xxlarge fatty\" rows=5 id=\"item{{$index}}\"></textarea>\n" +
    "						</span>\n" +
    "						<span ng-switch-when=\"select\" class=\"input-append\">\n" +
    "							<select name=\"item{{$index}}\" ng-model=\"item.value\" ng-options=\"value for value in item.options\" id=\"item{{$index}}\"></select>\n" +
    "						</span>\n" +
    "						<span ng-switch-when=\"dbdriver\" class=\"input-append\">\n" +
    "							<select name=\"item{{$index}}\" ng-style=\"{'width': {{dbTypeMaxLength}} + 'em'}\" ng-model=\"item.value\" id=\"item{{$index}}\">\n" +
    "								<option value=\"{{item.value}}\" ng-show=\"!arrayContains(item.value,dbTypeArray)\">{{item.value}}</option>\n" +
    "								<option ng-repeat=\"option in dbTypeArray\" value=\"{{option}}\">{{option}}</option>\n" +
    "							</select>\n" +
    "						</span>\n" +
    "						<span ng-switch-default>\n" +
    "							<pre>{{item|json}}</pre>\n" +
    "						</span>\n" +
    "						<span>\n" +
    "							<img src=\"images/loading.gif\" ng-show=\"item.busy\" />\n" +
    "							<img src=\"images/messages/error.png\" ng-show=\"item.error\" />\n" +
    "							<img src=\"images/messages/success.png\" ng-show=\"item.saved\" />\n" +
    "							<span class=\"error-message\">{{item.errorMessage}}</span>\n" +
    "						</span>\n" +
    "					</td>\n" +
    "					<td class=\"col-sm-3\">{{item.description}}</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				<tr>\n" +
    "					<td class=\"col-sm-2\"> </td>\n" +
    "					<td class=\"col-sm-7\" ng-switch=\"item.type\">\n" +
    "						<button class=\"btn btn-sm btn-primary\" type=\"button\" ng-click=\"saveConfigs(section,$index)\" ng-show=\"dbSettingsForm.$dirty\">Save Settings</button>\n" +
    "						<button class=\"btn btn-sm btn-success\" type=\"button\" ng-show=\"configsSaved && !dbSettingsForm.$dirty\"><span class=\"glyphicons white check\"></span> Saved</button>\n" +
    "					</td>\n" +
    "					<td class=\"col-sm-3\"> </td>\n" +
    "				</tr>\n" +
    "			</tbody>\n" +
    "			</table>\n" +
    "			</form>\n" +
    "		</div>\n" +
    "		</tab>\n" +
    "\n" +
    "	</tabset>\n" +
    "</div>\n" +
    "\n" +
    "\n" +
    "");
}]);

angular.module("counters/counters.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("counters/counters.tpl.html",
    "<div ng-show=\"initialLoading\" class=\"alert relative alert-info\">\n" +
    "	<img src=\"/img/spinner.gif\"> Loading Counters...\n" +
    "</div>\n" +
    "\n" +
    "<div ng-hide=\"initialLoading\">	\n" +
    "	<!-- <div class=\"row\">\n" +
    "		<div class=\"col-sm-12 text-right\"><label>Search: <input ng-model=\"searchText\"></label></div>\n" +
    "	</div> --> \n" +
    "	<p><button class=\"btn btn-sm btn-primary\" type=\"button\" ng-click=\"clear()\">Clear</button></p>\n" +
    "	\n" +
    "	<table class=\"table table-striped table-hover\">\n" +
    "		<tr>\n" +
    "			<th ng-click=\"sortType = 'timestamp'; sortReverse = !sortReverse\">\n" +
    "	           Timestamp \n" +
    "	            <span ng-show=\"sortType == 'timestamp' && !sortReverse\" class=\"caret\"></span>\n" +
    "	            <span ng-show=\"sortType == 'timestamp' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "			</th>\n" +
    "			<th  align=\"center\" ng-click=\"sortType = 'pageCount'; sortReverse = !sortReverse\">\n" +
    "	           Completed Reqs\n" +
    "	            <span ng-show=\"sortType == 'pageCount' && !sortReverse\" class=\"caret\"></span>\n" +
    "	            <span ng-show=\"sortType == 'pageCount' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span> \n" +
    "			</th>\n" +
    "			<th  align=\"center\" ng-click=\"sortType = 'avgPageTimeMs'; sortReverse = !sortReverse\">\n" +
    "	           Avg Req ms\n" +
    "	            <span ng-show=\"sortType == 'avgPageimeMs' && !sortReverse\" class=\"caret\"></span>\n" +
    "	            <span ng-show=\"sortType == 'avgPageimeMs' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "			</th>\n" +
    "			<th  align=\"center\" ng-click=\"sortType = 'queryCount'; sortReverse = !sortReverse\">\n" +
    "	           Completed Queries\n" +
    "	            <span ng-show=\"sortType == 'queryCount' && !sortReverse\" class=\"caret\"></span>\n" +
    "	            <span ng-show=\"sortType == 'queryCount' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "			</th>\n" +
    "			<th  align=\"center\" ng-click=\"sortType = 'avgQueryTimeMs'; sortReverse = !sortReverse\">\n" +
    "	           Avg Query ms\n" +
    "	            <span ng-show=\"sortType == 'avgQueryTimeMs' && !sortReverse\" class=\"caret\"></span>\n" +
    "	            <span ng-show=\"sortType == 'avgQueryTimeMs' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "			</th>\n" +
    "			<th  align=\"center\" ng-click=\"sortType = 'memoryUsedMiB'; sortReverse = !sortReverse\" title=\"Memory Used\">\n" +
    "	           Memory\n" +
    "	            <span ng-show=\"sortType == 'memoryUsedMiB' && !sortReverse\" class=\"caret\"></span>\n" +
    "	            <span ng-show=\"sortType == 'memoryUsedMiB' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "			</th>\n" +
    "			<th ng-click=\"sortType = 'uptime'; sortReverse = !sortReverse\">\n" +
    "	           Uptime\n" +
    "	            <span ng-show=\"sortType == 'uptime' && !sortReverse\" class=\"caret\"></span>\n" +
    "	            <span ng-show=\"sortType == 'uptime' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "			</th>\n" +
    "			<th  align=\"center\" ng-click=\"sortType = 'aactiveRequests'; sortReverse = !sortReverse\">\n" +
    "	           Active Reqs\n" +
    "	            <span ng-show=\"sortType == 'aactiveRequests' && !sortReverse\" class=\"caret\"></span>\n" +
    "	            <span ng-show=\"sortType == 'aactiveRequests' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "			</th>\n" +
    "		</tr>\n" +
    "	<!--\n" +
    "	{\"duration\":10,\"pageCount\":0,\"memoryUsedMiB\":38,\"avgQueryTimeMs\":0,\"memoryTotalMiB\":274,\"avgPageTimeMs\":0,\"queryCount\":0,\"memoryAvailableMiB\":235,\"loadAverage\":-1,\"activeRequests\":0,\"timestamp\":1463669475566,\"uptime\":\"10:28.8\"}\n" +
    "	-->\n" +
    "		<tr ng-repeat=\"counter in counters | orderBy:sortType:sortReverse | startFrom:(currentPage-1)*pageSize | limitTo:pageSize\"> \n" +
    "			<td align=\"center\">{{counter.timestamp | date: 'MMM dd yyyy h:mm:ss a' }}</td>\n" +
    "			<td align=\"center\">{{counter.pageCount}}</td>\n" +
    "			<td align=\"center\">{{counter.avgPageTimeMs}}</td>\n" +
    "			<td align=\"center\">{{counter.queryCount}}</td>\n" +
    "			<td align=\"center\">{{counter.avgQueryTimeMs}}</td>\n" +
    "			<td align=\"center\"> \n" +
    "				<span ng-show=\"counter.memoryUsedMiB >= 2000\">{{counter.memoryUsedMiB/1000}} GB</span>\n" +
    "				<span ng-show=\"counter.memoryUsedMiB < 2000\">{{counter.memoryUsedMiB}} MB</span></td>\n" +
    "			<td align=\"center\">{{counter.uptime}}</td>\n" +
    "			<td align=\"center\">{{counter.activeRequests}}</td>\n" +
    "		</tr>\n" +
    "	</table>\n" +
    "\n" +
    "	<pagination ng-model=\"currentPage\" total-items=\"totalItems\" items-per-page=\"pageSize\" class=\"pagination-sm\" ng-show=\"noOfPages > 1\"></pagination>\n" +
    "</div>");
}]);

angular.module("dashboard/dashboard.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("dashboard/dashboard.tpl.html",
    "<div ng-show=\"isEnterprise\">\n" +
    "	<div class=\"content content-wide\">\n" +
    "		<ul class=\"nav nav-tabs\">\n" +
    "		  <li ng-class=\"{active:$state.includes('dashboard.all')}\"><a ui-sref=\"dashboard.all\">All</a></li>\n" +
    "		  <li ng-class=\"{active:$state.includes('dashboard.problems')}\"><a ui-sref=\"dashboard.problems\">Problems</a></li>\n" +
    "		  <li ng-class=\"{active:$state.includes('dashboard.incidents') && !$state.includes('dashboard.incidents.detail')}\"><a ui-sref=\"dashboard.incidents\">Incidents</a></li>\n" +
    "		  <li ng-show=\"$state.includes('dashboard.incidents.detail')\" ng-class=\"{active:$state.includes('dashboard.incidents.detail')}\"><a name=\"details\" ui-sref=\"dashboard.incidents\">Incident Details <span class=\"glyphicons remove leftMargin handcursor\"></span></a></li>\n" +
    "	\n" +
    "		</ul>\n" +
    "	</div>\n" +
    "	<div class=\"push-down\" ui-view></div>\n" +
    "</div>\n");
}]);

angular.module("dashboard/incident.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("dashboard/incident.tpl.html",
    "<div ng-hide=\"incident\" class=\"alert relative alert-info\" role=\"alert\">\n" +
    "  No data to show for this incident..\n" +
    "</div>\n" +
    "\n" +
    "<div ng-show=\"incident\">\n" +
    "\n" +
    "<div class=\"col-sm-2 text-right\">Incident Label:</div>\n" +
    "<div class=\"col-sm-10\">{{incident.incidentType}}</div>\n" +
    "\n" +
    "<div class=\"col-sm-2 text-right\">Logged at:</div>\n" +
    "<div class=\"col-sm-10\">{{incident.beginTime | date: 'MMM dd yyyy h:mm:ss a'}}</div>\n" +
    "\n" +
    "<div class=\"col-sm-2 text-right\">Number of Requests:</div>\n" +
    "<div class=\"col-sm-10\">{{incident.requests.length}}</div>\n" +
    "\n" +
    "<div class=\"row\" ng-show=\"incident.requests\">\n" +
    "	<div class=\"col-sm-2 text-right\">\n" +
    "		<h4>Logged Requests: </h4>\n" +
    "	</div> \n" +
    "	<div class=\"col-sm-10\">\n" +
    "		<div ng-repeat=\"request in incident.requests\" class=\"padded\"\n" +
    "			ng-class-odd=\"'odd'\" ng-class-even=\"'even'\">\n" +
    "\n" +
    "			<div class=\"row\" ng-show=\"request.url\">\n" +
    "				<div class=\"col-sm-2 text-right\">Request URL:</div>\n" +
    "				<div class=\"col-sm-10\"><a href=\"{{request.url}}\" target=\"top\">{{request.url}}</a></div>\n" +
    "			</div>\n" +
    "\n" +
    "			<div class=\"row\" ng-show=\"!request.url && request.requestURI\">\n" +
    "				<div class=\"col-sm-2 text-right\">Request URI:</div>\n" +
    "				<div class=\"col-sm-10\">{{request.requestURI}}</div>\n" +
    "			</div>\n" +
    "\n" +
    "			<div class=\"row\" ng-show=\"request.method\">\n" +
    "				<div class=\"col-sm-2 text-right\">HTTP Method:</div>\n" +
    "				<div class=\"col-sm-10\">{{request.method}}</div>\n" +
    "			</div>\n" +
    "\n" +
    "			<div class=\"row\" ng-show=\"request.threadName\">\n" +
    "				<div class=\"col-sm-2 text-right\">Thread Name:</div>\n" +
    "				<div class=\"col-sm-10\">{{request.threadName}}</div>\n" +
    "			</div>\n" +
    "\n" +
    "			<div class=\"row\" ng-show=\"request.time\">\n" +
    "				<div class=\"col-sm-2 text-right\">Execution Time:</div>\n" +
    "				<div class=\"col-sm-10\">{{request.time}}</div>\n" +
    "			</div>\n" +
    "\n" +
    "			<div class=\"row\" ng-show=\"request.timeToFirstByte\">\n" +
    "				<div class=\"col-sm-2 text-right\">Time to 1st Byte:</div>\n" +
    "				<div class=\"col-sm-10\">{{request.timeToFirstByte}}</div>\n" +
    "			</div>\n" +
    "\n" +
    "			<div class=\"row\" ng-show=\"request.longQueryActive\">\n" +
    "				<div class=\"col-sm-2 text-right\">Longest SQL Query:</div>\n" +
    "				<div class=\"col-sm-10\">\n" +
    "				<p>{{toDurationString(request.longQueryElapsed)}}Ms</p>\n" +
    "				<p>{{request.longQueryRows}} Rows</p>\n" +
    "				<p style=\"font-family: monospace;\">{{request.longQuerySql}}</p>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "\n" +
    "			<div class=\"row\" ng-show=\"request.stack\">\n" +
    "				<div class=\"col-sm-2 text-right\">Stack Trace:</div>\n" +
    "				<div class=\"col-sm-10\"><pre>{{request.stack}}</pre></div>\n" +
    "			</div>\n" +
    "\n" +
    "			<div class=\"row\" ng-show=\"request.ip\">\n" +
    "				<div class=\"col-sm-2 text-right\">IP Address:</div>\n" +
    "				<div class=\"col-sm-10\">{{request.ip}}</div>\n" +
    "			</div>\n" +
    "\n" +
    "			<div class=\"row\" ng-show=\"request.method\">\n" +
    "				<div class=\"col-sm-2 text-right\">HTTP Method:</div>\n" +
    "				<div class=\"col-sm-10\">{{request.method}}</div>\n" +
    "			</div>\n" +
    "		</div>\n" +
    "	</div>\n" +
    "</div>");
}]);

angular.module("dashboard/incidents.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("dashboard/incidents.tpl.html",
    "<div ui-view ng-show=\"$state.includes('dashboard.incidents.detail')\"></div>\n" +
    "\n" +
    "<div ng-hide=\"$state.includes('dashboard.incidents.detail')\">\n" +
    "\n" +
    "<div ng-show=\"initialLoading\" class=\"alert relative alert-info\">\n" +
    "	<img src=\"/img/spinner.gif\"> Loading incidents...\n" +
    "</div>\n" +
    "\n" +
    "<div ng-hide=\"initialLoading\">\n" +
    "\n" +
    "	<div ng-hide=\"incidents.length\" class=\"alert relative alert-info\" role=\"alert\">\n" +
    "	  No incidents to list right now...\n" +
    "	</div>\n" +
    "\n" +
    "	<div ng-show=\"incidents.length\">		\n" +
    "			<!-- <div class=\"row\">\n" +
    "				<div class=\"col-sm-12 text-right\"><label>Search: <input ng-model=\"searchText\"></label></div>\n" +
    "			</div> -->\n" +
    "		\n" +
    "			<table class=\"table table-striped table-hover\">\n" +
    "				<tr>\n" +
    "					<th>Label</th>\n" +
    "					<th>Start Date</th>\n" +
    "					<th>End Date</th>\n" +
    "					<th>Threshold Type</th>\n" +
    "					<th>Action Taken</th>\n" +
    "					<th></th>\n" +
    "				</tr>\n" +
    "				<!--  -->\n" +
    "				<tr ng-repeat=\"incident in incidents | startFrom:(currentPage-1)*pageSize | limitTo:pageSize\" ng-click=\"setSelectedIncident(incident.incidentID)\" ui-sref=\"dashboard.incidents.detail({id:incident.incidentID})\"  ng-class=\"{highlighted: incident.incidentID === idSelectedIncident}\"> \n" +
    "				<!-- <tr ng-repeat=\"incident in filtered = incidents | filter:searchText | startFrom:(currentPage-1)*pageSize | limitTo:pageSize\">-->\n" +
    "					<td>{{incident.incidentType}}</td>\n" +
    "					<td>{{incident.beginTime | date: 'MMM dd yyyy h:mm a'}}</td>\n" +
    "					<td>{{incident.endTime | date: 'MMM dd yyyy h:mm a'}}</td>\n" +
    "					<td>{{incident.thresholdType}} \n" +
    "					({{incident.thresholdValue}} \n" +
    "					<span ng-switch on=\"incident.thresholdType\">\n" +
    "					  <span ng-switch-when=\"ACTIVEREQ\">)</span>\n" +
    "					  <span ng-switch-when=\"\">)</span>\n" +
    "					  <span ng-switch-when=\"MEMORYPCT\">%)</span>\n" +
    "					  <span ng-switch-default>Ms)</span>\n" +
    "					</span>\n" +
    "					</td>\n" +
    "					<td>{{incident.actionTaken}}</td>\n" +
    "					<td>\n" +
    "						<span class=\"glyphbutton glyphicons remove linkish\" ng-click=\"removeIncident(incident.incidentID)\" title=\"Delete this Incident\"></span>\n" +
    "					</td>\n" +
    "					\n" +
    "				</tr>\n" +
    "			</table>\n" +
    "\n" +
    "			<pagination ng-model=\"currentPage\" total-items=\"totalItems\" items-per-page=\"pageSize\" class=\"pagination-sm\" ng-show=\"noOfPages > 1\"></pagination>\n" +
    "	</div>\n" +
    "</div>\n" +
    "</div> ");
}]);

angular.module("dashboard/servers.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("dashboard/servers.tpl.html",
    "<div ng-show=\"initialLoading\" class=\"alert relative alert-info\">\n" +
    "	<img src=\"/img/spinner.gif\"> Loading server list...\n" +
    "</div>\n" +
    "\n" +
    "<div ng-hide=\"initialLoading\">\n" +
    "	<div ng-hide=\"servers.length\" class=\"alert relative alert-info\" role=\"alert\">\n" +
    "	  No servers to list right now...\n" +
    "	</div>\n" +
    "\n" +
    "	<table class=\"table table-striped table-hover table-condensed\" ng-show=\"servers.length\">\n" +
    "	<thead>\n" +
    "		<tr>\n" +
    "			<th>Server</th>\n" +
    "			<th>Status</th>\n" +
    "			<th>Uptime</th>\n" +
    "			<th width=\"100\">Memory</th>\n" +
    "			<th width=\"100\">Active Pages</th>\n" +
    "			<th width=\"100\">Pages/Sec</th>\n" +
    "			<th width=\"100\">Qrys/Sec</th>\n" +
    "			<th width=\"100\">Avg Page Ms</th>\n" +
    "			<th width=\"100\">Avg Qry Ms</th>\n" +
    "		</tr>\n" +
    "	</thead>\n" +
    "	<tbody>\n" +
    "		<tr ng-repeat=\"server in servers\" ng-click=\"openServer(server);\">\n" +
    "			<td>{{server.name}}</td>\n" +
    "			<td>{{server.status}}</td>\n" +
    "			<td>{{server.uptime}}</td>\n" +
    "			<td><progress-bar cur=\"$parent.server.memory.used\" max=\"$parent.server.memory.total\" title=\"{{mb($parent.server.memory.used)}}M / {{mb($parent.server.memory.total)}}M\">{{(server.memory.used/server.memory.total*100) | number:1}}%</progress-bar>\n" +
    "			</td>\n" +
    "			<td><progress-bar cur=\"$parent.server.numcurrentrequests\" max=\"$parent.server.maxcurrentrequests\">{{server.numcurrentrequests}}/{{server.maxcurrentrequests}}</progress-bar></td>\n" +
    "			<td><progress-bar cur=\"$parent.server.pps\" max=\"100\">{{server.pps | number:1}}</progress-bar></td>\n" +
    "			<td><progress-bar cur=\"$parent.server.qps\" max=\"1000\">{{server.qps | number:1}}</progress-bar></td>\n" +
    "			<td><progress-bar cur=\"$parent.server.avgpagetime\" max=\"$parent.server.slowpagethreshold\" title=\"{{$parent.server.avgpagetime}}/{{$parent.server.slowpagethreshold}}\">{{server.avgpagetime | number}}</progress-bar></td>\n" +
    "			<td><progress-bar cur=\"$parent.server.avgqrytime\" max=\"$parent.server.slowquerythreshold\" title=\"{{$parent.server.avgqrytime}}/{{$parent.server.slowqrythreshold}}\">{{server.avgqrytime | number}}</progress-bar></td>\n" +
    "		</tr>\n" +
    "	</tbody>\n" +
    "	</table>\n" +
    "</div>");
}]);

angular.module("dos/dos.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("dos/dos.tpl.html",
    "<div class=\"container-fluid\">\n" +
    "	<div class=\"row\">\n" +
    "		<div class=\"col-sm-12 col-md-6 col-lg-8\" ng-include=\"'dos/settings.tpl.html'\"></div>\n" +
    "		<div class=\"col-sm-12 col-md-6 col-lg-4\" ng-include=\"'dos/list.tpl.html'\"></div>\n" +
    "	</div>\n" +
    "</div>");
}]);

angular.module("dos/list.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("dos/list.tpl.html",
    "<div class=\"panel panel-default\" ng-show=\"addingExclusion\">\n" +
    "	<div class=\"panel-heading\">\n" +
    "		<h3 class=\"panel-title\">Add an IP Addresses</h3>\n" +
    "	</div>\n" +
    "	<div class=\"content panel-body\">\n" +
    "		<form class=\"form-horizontal\" name=\"dos_exclusion\"> \n" +
    "			<p>This IP address will <u>never</u> get banned</p>\n" +
    "			<input type=\"text\" id=\"ip\" name=\"ip\" ng-model=\"newExclusion.ip\" placeholder=\"New IP Address\" ng-pattern='/^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/'>\n" +
    "			<button class=\"btn btn-sm btn-primary\" type=\"button\" ng-click=\"insertExclusion()\">Add IP</button>\n" +
    "			<button class=\"btn btn-sm btn-primary\" type=\"button\" ng-click=\"closeExclusion()\">Cancel</button>\n" +
    "		</form>\n" +
    "	</div>\n" +
    "</div>\n" +
    "\n" +
    "\n" +
    "<div class=\"nopadding panel panel-default\">\n" +
    "	<div class=\"panel-heading\">\n" +
    "		<h3 class=\"panel-title\">Banned IP Addresses</h3>\n" +
    "	</div>\n" +
    "	<div class=\"content panel-body\">\n" +
    "		<tabset class=\"config\">\n" +
    "			<tab ng-repeat=\"ipList in blockLists\" heading=\"{{ipList.label}}\" data-spy=\"scroll\" active=\"ipList.isActive\">\n" +
    "				<a name=\"{{ipList.label}}\"></a>\n" +
    "				<div class=\"table-responsive\">\n" +
    "					<table class=\"table table-striped table-hover\">\n" +
    "						<tbody>\n" +
    "							<tr>\n" +
    "								<th>{{ipList.hint}}</th>\n" +
    "								<th ng-show=\"ipList.isExclude\"><span class=\"glyphbutton glyphicons plus linkish\" ng-click=\"addExclusion()\" title=\"Add an exclusion IP address\"></span></th>\n" +
    "							</tr>\n" +
    "							<tr ng-repeat=\"ip in dosStatus[ipList.element]\">\n" +
    "								<td>{{ip}}</td>\n" +
    "								<td>\n" +
    "									<span class=\"glyphbutton glyphicons remove linkish\" ng-click=\"removeIP(ipList.element,ip)\" title=\"Remove this IP\"></span>\n" +
    "								</td>\n" +
    "							</tr>\n" +
    "							<tr ng-show=\"dosStatus[ipList.element].length==0\">\n" +
    "								<td colspan=\"2\"><div class=\"alert relative alert-info\" role=\"alert\">No IPs Recorded</div></td>\n" +
    "							</tr>\n" +
    "						</tbody>\n" +
    "					</table>\n" +
    "				</div>\n" +
    "			</tab>\n" +
    "		</tabset>\n" +
    "	</div>\n" +
    "</div>\n" +
    "");
}]);

angular.module("dos/settings.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("dos/settings.tpl.html",
    "<div class=\"nopadding panel panel-default\">\n" +
    "<div class=\"panel-heading\">\n" +
    "	<h3 class=\"panel-title\">Settings</h3>\n" +
    "</div>\n" +
    "<div class=\"content panel-body\">\n" +
    "	<form class=\"form-horizontal\" name=\"dos_settings\"> \n" +
    "		<table class=\"table table-striped table-hover\">\n" +
    "			<tbody>\n" +
    "				<tr>\n" +
    "					<td class=\"col-sm-4\"><strong>DOS Protection Enabled?</strong></td>\n" +
    "					<td class=\"col-sm-8\">\n" +
    "						<toggle-switch class=\"switch-success\" ng-model=\"dosConfig.enabled\" on-label=\"Enabled\" off-label=\"Disabled\" ng-value=\"dosConfig.enabled\"></toggle-switch>\n" +
    "						<p class=\"hint\">\n" +
    "							Turn Denial of Service Protection On/Off\n" +
    "						</p>\n" +
    "					</td>\n" +
    "				</tr>\n" +
    "				<tr>\n" +
    "					<td class=\"col-sm-4\"><strong>Request Limit</strong></td>\n" +
    "					<td class=\"col-sm-8\">\n" +
    "						<input name=\"requestlimit\" numericbinding type=\"number\" class=\"input-mini\" ng-model=\"dosConfig.requestlimit\" id=\"requestlimit\"> \n" +
    "						<p class=\"hint\">\n" +
    "							Number of concurrent requests to trigger a ban (from the same IP)\n" +
    "						</p>\n" +
    "					</td>\n" +
    "				</tr>\n" +
    "				<tr>\n" +
    "					<td class=\"col-sm-4\"><strong>Ban Duration</strong></td>\n" +
    "					<td class=\"col-sm-8\">\n" +
    "						<input name=\"bandurationmin\" numericbinding type=\"number\" class=\"input-mini\" ng-model=\"dosConfig.bandurationmin\" id=\"bandurationmin\"> minutes\n" +
    "						<p class=\"hint\">\n" +
    "							Duration of a temporary ban (minutes)\n" +
    "						</p>\n" +
    "					</td>\n" +
    "				</tr>\n" +
    "				<tr>\n" +
    "					<td class=\"col-sm-4\"><strong>Protection Action</strong></td>\n" +
    "					<td class=\"col-sm-8\">\n" +
    "						<div ng-repeat=\"option in protectionOptions\">\n" +
    "						<label><input type=\"radio\" ng-model=\"dosConfig.action\" ng-value=\"option.element\"> {{option.label}}: </label>\n" +
    "						{{option.hint}}\n" +
    "						</div>\n" +
    "					</td>\n" +
    "				</tr>\n" +
    "				<tr>\n" +
    "					<td class=\"col-sm-4\"> </td>\n" +
    "					<td class=\"col-sm-8\" ng-switch=\"item.type\"><button class=\"btn btn-sm btn-primary\" type=\"button\" ng-click=\"saveConfig()\">Save Settings</button></td>\n" +
    "				</tr>\n" +
    "			</tbody>\n" +
    "		</table>\n" +
    "	</form>\n" +
    "</div>\n" +
    "</div>");
}]);

angular.module("header.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("header.tpl.html",
    "<div class=\"container-fluid\">\n" +
    "	<div class=\"row data-widgets\" style=\"margin-top:5px;\">\n" +
    "		<div class=\"col-sm-12 col-md-4 col-lg-4\" ng-if=\"isAuthenticated\">\n" +
    "			<sf-metric-widget metrics=\"serverMetrics\" showcontrols=\"true\" title=\"serverNameTitle\" gc=\"gc()\" refresh=\"refresh()\" setPodRefresh=\"setpodrefresh(sec)\" \n" +
    "			refreshTime=\"podtimeout\"></sf-metric-widget>\n" +
    "		</div>\n" +
    "		<div class=\"col-sm-12 col-md-4 col-lg-4\" ng-if=\"isAuthenticated\">\n" +
    "			<sf-metric-widget metrics=\"requestMetrics\" title=\"'Requests'\"></sf-metric-widget>\n" +
    "		</div>\n" +
    "		<div class=\"col-sm-12 col-md-4 col-lg-4\" ng-if=\"isAuthenticated\">\n" +
    "			<sf-metric-widget metrics=\"queryMetrics\" title=\"'Queries'\"></sf-metric-widget>\n" +
    "		</div>\n" +
    "		<!-- <div class=\"hidden-xs hidden-sm hidden-md hidden-lg text-muted\" ng-if=\"isAuthenticated\">\n" +
    "			<span ng-show=\"hasPendingRequests()\" class=\"glyphicons heart\"></span>\n" +
    "			<span ng-hide=\"hasPendingRequests()\" class=\"glyphicons heart_empty\"></span>\n" +
    "		</div> -->\n" +
    "	</div>\n" +
    "</div>\n" +
    "\n" +
    "<div class=\"breadcrumb\" style=\"position:relative;\">{{breadcrumb}} <img src=\"SeeFusion.png\" class=\"logo\" border=\"0\" style=\"position:absolute;right:0px;top:0px;\"/></div>\n" +
    "\n" +
    "<!-- <button type=\"btn\" ng-show=\"!showNav\" ng-click=\"toggleNav()\"><span class=\"glyphicons resize_full\"></span> Show Menu</button> -->\n" +
    "\n" +
    "<div class=\"push-down\">\n" +
    "	<alert ng-repeat=\"alert in alerts\" type=\"{{alert.type}}\" close=\"closeAlert(alert)\">{{ alert.msg }}</alert>\n" +
    "</div>");
}]);

angular.module("help/help.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("help/help.tpl.html",
    "<h1>Help</h1>\n" +
    "\n" +
    "<img src=\"images/loading.gif\" ng-show=\"busy\">\n" +
    "<p ng-hide=\"busy\">Version {{about.version}} build {{about.buildNumber}}</p>\n" +
    "<p ng-hide=\"busy\">Licensed to: {{about.license}}</p>\n");
}]);

angular.module("info/connection-modal.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("info/connection-modal.tpl.html",
    "<div class=\"modal-header\">\n" +
    "	<h3 class=\"modal-title\">Connection Problem</h3>\n" +
    "</div>\n" +
    "<div class=\"modal-body\">\n" +
    "\n" +
    "	Connection Problem...\n" +
    "\n" +
    "</div>");
}]);

angular.module("info/connectivity.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("info/connectivity.tpl.html",
    "<div class=\"col-xs-2\">\n" +
    "<span class=\"glyphicons certificate grey\" style=\"font-size:200px\"></span>\n" +
    "</div>\n" +
    "<div class=\"col-xs-10\" style=\"margin-top:2em;\">\n" +
    "<p>\n" +
    "We're having a problem connecting to your server.\n" +
    "</p><p>\n" +
    "Please make sure your application server is running.\n" +
    "</p>\n" +
    "\n" +
    "<p><button class=\"btn btn-sm btn-primary\" type=\"button\" ng-click=\"retry()\">Retry Connection</button></p>\n" +
    "</div>");
}]);

angular.module("info/info.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("info/info.tpl.html",
    "<div ui-view></div>");
}]);

angular.module("info/license.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("info/license.tpl.html",
    "");
}]);

angular.module("log/log.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("log/log.tpl.html",
    "<div class=\"panel\">\n" +
    "	<div class=\"content\">\n" +
    "		<pre>{{log}}</pre>\n" +
    "	</div>\n" +
    "</div>");
}]);

angular.module("login/login-modal.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("login/login-modal.tpl.html",
    "<div class=\"modal-header\">\n" +
    "	<h3 class=\"modal-title\">Login</h3>\n" +
    "</div>\n" +
    "<div class=\"modal-body\">\n" +
    "\n" +
    "	<form class=\"form-horizontal\" name=\"form\" novalidate ng-submit=\"login()\">\n" +
    "\n" +
    "		<div class=\"form-group has-feedback\" ng-class=\"{'has-error':form.password.$invalid && form.password.$dirty, 'has-success':form.password.$valid}\">\n" +
    "			<label for=\"password\" class=\"col-sm-2 control-label\">Password</label>\n" +
    "			<div class=\"col-sm-4\">\n" +
    "				<input id=\"loginpassword\" type=\"password\" class=\"form-control\" name=\"password\" placeholder=\"Password\" required ng-model=\"password\">\n" +
    "				<span class=\"glyphicons ok form-control-feedback\"></span>\n" +
    "			</div>\n" +
    "		</div>\n" +
    "\n" +
    "	<form>\n" +
    "\n" +
    "</div>\n" +
    "<div class=\"modal-footer\">\n" +
    "	<button ng-disabled=\"form.$invalid\" type=\"submit\" class=\"btn btn-primary\" ng-click=\"login()\"><span class=\"glyphicons white check\"></span>Login</button>\n" +
    "	<button class=\"btn btn-default\" ng-click=\"cancel()\">Cancel</button>\n" +
    "</div>\n" +
    "");
}]);

angular.module("monitoring/edit.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("monitoring/edit.tpl.html",
    "<form class=\"form-horizontal\" name=\"ruleForm\">\n" +
    "	<div class=\"form-group\" ng-class=\"{'has-error' : ruleForm.inputName.$dirty && ruleForm.inputName.$invalid}\">\n" +
    "		<label class=\"col-sm-2 control-label\" for=\"inputName\">Name</label>\n" +
    "		<div class=\"col-sm-8\">\n" +
    "			<input type=\"text\" id=\"inputName\" name=\"inputName\" placeholder=\"Rule Name...\" ng-model=\"rule.name\" ng-required=\"inputName\" ng-pattern=\"/^[a-zA-Z0-9 ]*$/\" class=\"form-control\" />\n" +
    "			<div role=\"alert\" class=\"errorHighlight\" ng-show=\"ruleForm.inputName.$dirty\">\n" +
    "      		<span  ng-show=\"ruleForm.inputName.$error.required\">Please enter a rule name</span>\n" +
    "      		<span  ng-show=\"ruleForm.inputName.$error.pattern\">Rule name can only contain letters, numbers and spaces</span>\n" +
    "    		</div>\n" +
    "		</div>\n" +
    "	</div>\n" +
    "\n" +
    "	<div class=\"form-group\">\n" +
    "		<label class=\"col-sm-2 control-label\" for=\"isEnabled\">Enabled</label>\n" +
    "		<div class=\"col-sm-8 controls\">\n" +
    "			<toggle-switch class=\"switch-success\" ng-model=\"rule.isEnabled\" on-label=\"Enabled\" off-label=\"Disabled\"></toggle-switch>\n" +
    "		</div>\n" +
    "	</div>\n" +
    "\n" +
    "	<div class=\"form-group\" ng-class=\"{'has-error' : ruleForm.inputtriggerType.$dirty && ruleForm.inputtriggerType.$invalid}\">\n" +
    "		<label class=\"col-sm-2 control-label\" for=\"inputtriggerType\">Triggered By</label>\n" +
    "		<div class=\"col-sm-8\">\n" +
    "			<select id=\"inputtriggerType\" name=\"inputtriggerType\" ng-model=\"rule.triggerType\" ng-options=\"r.value as r.label for r in ruleTypes\" ng-required class=\"form-control\">\n" +
    "				<option value=\"\">Select a Trigger Type</option>\n" +
    "			</select>\n" +
    "			<div role=\"alert\" class=\"errorHighlight\" ng-show=\"ruleForm.inputtriggerType.$dirty\">\n" +
    "      		<span ng-show=\"ruleForm.inputtriggerType.$error.required\">Please choose a trigger type</span>\n" +
    "    		</div>\n" +
    "		</div>\n" +
    "	</div>\n" +
    "\n" +
    "	<div class=\"form-group\" ng-class=\"{'has-error' : ruleForm.inputtriggerLimit.$dirty && ruleForm.inputtriggerLimit.$invalid}\">\n" +
    "		<label class=\"col-sm-2 control-label\" for=\"inputtriggerLimit\">Threshold</label>\n" +
    "		<div class=\"col-sm-8 controls\">\n" +
    "			<input type=\"number\" id=\"inputtriggerLimit\" name=\"inputtriggerLimit\" ng-model=\"rule.triggerLimit\" class=\"input-mini\" ng-pattern=\"/^[0-9]*$/\" ng-required>\n" +
    "			<span ng-switch on=\"rule.triggerType\">\n" +
    "			  <span ng-switch-when=\"ACTIVEREQ\"> </span>\n" +
    "			  <span ng-switch-when=\"\"> </span>\n" +
    "			  <span ng-switch-when=\"MEMORYPCT\">%</span>\n" +
    "			  <span ng-switch-default>Ms</span>\n" +
    "			</span>	\n" +
    "			<span class=\"help-block\">This is the threshold at which this rule is triggered</span> \n" +
    "			<div role=\"alert\" class=\"errorHighlight\" ng-show=\"ruleForm.inputtriggerLimit.$dirty\">\n" +
    "      			<span ng-show=\"ruleForm.inputtriggerLimit.$invalid\">The threshold value must be a number</span>\n" +
    "    		</div>\n" +
    "		</div>\n" +
    "\n" +
    "	</div>\n" +
    "\n" +
    "	<div class=\"form-group\">\n" +
    "		<label class=\"col-sm-2 control-label\" for=\"inputexcludeURLs\">Exclude URLs</label>\n" +
    "		<div class=\"col-sm-8 controls\">\n" +
    "			<!-- <div ng-repeat=\"u in rule.excludeURLs\">\n" +
    "				<span class=\"glyphbutton glyphicons remove\" ng-click=\"removeExclude(u)\" title=\"Remove this exclusion URL\"></span>\n" +
    "				<span ng-bind=\"u\"></span>\n" +
    "			</div>\n" +
    "			<div class=\"controls\">\n" +
    "				<input type=\"text\" ng-model=\"newExclude\">\n" +
    "				<button class=\"btn btn-sm btn-primary\" ng-click=\"addExclude()\">Add</button>\n" +
    "			</div> -->\n" +
    "			<div class=\"controls\">\n" +
    "				<textarea id=\"inputexcludeURLs\" name=\"inputexcludeURLs\" split-array ng-model=\"rule.excludeURLs\" class=\"form-control\" rows=5></textarea>\n" +
    "			</div>\n" +
    "			<span class=\"help-block\">Request-based rules that match these substrings will be ignored (one per line)</span>\n" +
    "		</div>\n" +
    "	</div>\n" +
    "\n" +
    "	<div class=\"form-group\">\n" +
    "		<label class=\"col-sm-2 control-label\" for=\"inputDelay\">Delay</label>\n" +
    "		<div class=\"col-sm-8 controls\">\n" +
    "			<input type=\"number\" id=\"inputDelay\" placeholder=\"zzz\" ng-model=\"rule.delay\" class=\"input-mini\" ng-pattern=/^[0-9]*$/> seconds\n" +
    "			<span class=\"help-block\">This is the amount of time the rule must be \"active\" before action or notification</span>\n" +
    "		</div>\n" +
    "	</div>\n" +
    "\n" +
    "	<div class=\"form-group\">\n" +
    "		<label class=\"col-sm-2 control-label\" for=\"isKillResponse\">Action</label>\n" +
    "		<div class=\"col-sm-8 controls\">\n" +
    "			<!-- <input type=\"checkbox\" id=\"isLogResponse\" ng-model=\"rule.isLogResponse\"><label for=\"isLogResponse\"> Log Event</label><br/> --> <!-- DB Took this out, but not sure if that's going to be permanent -->\n" +
    "			<input type=\"checkbox\" id=\"isKillResponse\" ng-model=\"rule.isKillResponse\"> <label for=\"isKillResponse\"> Kill (Oldest) Page</label>\n" +
    "		</div>\n" +
    "	</div>\n" +
    "	\n" +
    "	<div class=\"form-group\">\n" +
    "		<label class=\"col-sm-2 control-label\" for=\"inputsleep\">Sleep</label>\n" +
    "		<div class=\"col-sm-8 controls\">\n" +
    "			<span><input type=\"number\" id=\"inputsleep\" placeholder=\"zzzz\" ng-model=\"rule.sleep\" class=\"input-mini\" ng-pattern=/^[0-9]*$/ > minutes</span>\n" +
    "			<span class=\"help-block\">This is the delay between repeated triggers of this rule</span>\n" +
    "		</div>\n" +
    "	</div>\n" +
    "\n" +
    "	<div class=\"form-group\"> \n" +
    "		<label class=\"col-sm-2 control-label\" for=\"isNotifyResponse\">Notify</label>\n" +
    "		<div class=\"col-sm-8 controls\">\n" +
    "			<input type=\"checkbox\" id=\"isNotifyResponse\" ng-model=\"rule.isNotifyResponse\"> <label for=\"isNotifyResponse\"> Send Notification</label>\n" +
    "		</div>\n" +
    "	</div>\n" +
    "\n" +
    "	<div class=\"form-group\" ng-show=\"rule.isNotifyResponse\">\n" +
    "		<label class=\"col-sm-2 control-label\" for=\"inputsmtpFrom\">Mail From:</label>\n" +
    "		<div class=\"col-sm-8 controls\">\n" +
    "			<input type=\"text\" id=\"inputsmtpFrom\" placeholder=\"alerts@mydomain.com\" ng-model=\"rule.smtpFrom\">\n" +
    "		</div>\n" +
    "	</div>\n" +
    "\n" +
    "	<div class=\"form-group\" ng-show=\"rule.isNotifyResponse\">\n" +
    "		<label class=\"col-sm-2 control-label\" for=\"inputsmtpTo\">Send To:</label>\n" +
    "		<div class=\"col-sm-8 controls\">\n" +
    "			<input type=\"text\" id=\"inputsmtpTo\" placeholder=\"name@mydomain.com,name2@mydomain.com...\" ng-model=\"rule.smtpTo\">\n" +
    "			<span class=\"help-block\">Whitespace- or comma-delimited list</span>\n" +
    "		</div>\n" +
    "	</div>\n" +
    "\n" +
    "	<div class=\"form-group\" ng-show=\"rule.isNotifyResponse\">\n" +
    "		<label class=\"col-sm-2 control-label\" for=\"inputsmtpSubject\">Subject</label>\n" +
    "		<div class=\"col-sm-8 controls\">\n" +
    "			<input type=\"text\" id=\"inputsmtpSubject\" placeholder=\"The sky is falling..!\" ng-model=\"rule.smtpSubject\">\n" +
    "			<span class=\"help-block\">\n" +
    "				You can include the following variables in the Subject of the message:<br> \n" +
    "				{name} : The name of the rule.<br>\n" +
    "				{reason} : The reason the rule was hit (for example, \"Current Request Count 15 >= limit of 15\")<br>\n" +
    "				{action} : The action(s) performed.<br>\n" +
    "				<!--{uri} : The request that triggered this rule (or the oldest active request for Server rules)<br>\n" +
    "				{query} : Information about the currently active query in the [oldest] active page.<br> -->\n" +
    "			</span>\n" +
    "					\n" +
    "		</div>\n" +
    "	</div>\n" +
    "\n" +
    "	<div class=\"form-group\" ng-show=\"rule.isNotifyResponse\">\n" +
    "		<label class=\"col-sm-2 control-label\" for=\"inputnotifyDisableMin\">notifyDisableMin</label>\n" +
    "		<div class=\"col-sm-8 controls\">\n" +
    "			<input type=\"number\" id=\"inputnotifyDisableMin\" placeholder=\"\" ng-model=\"rule.notifyDisableMin\" class=\"input-mini\" ng-pattern=/^[0-9]*$/>min\n" +
    "			<span class=\"help-block\">This is the amount of that must elapse between notifications.</span>\n" +
    "		</div>\n" +
    "	</div>\n" +
    "	<div class=\"form-group\">\n" +
    "		<div class=\"col-sm-8 controls\">\n" +
    "			<button type=\"submit\" class=\"btn btn-primary\" ng-click=\"submitted=true;save();\" href=\"#\" onClick=\"return false;\">Save</button>\n" +
    "			<button type=\"submit\" class=\"btn btn-default\" ui-sref=\"monitoring.rules\">Cancel</button>\n" +
    "			<img src=\"images/loading.gif\" ng-show=\"saveBusy\" />\n" +
    "			<img src=\"images/messages/error.png\" ng-show=\"saveNotOK\" />{{saveErr}}\n" +
    "		</div>\n" +
    "	</div>\n" +
    "</form>");
}]);

angular.module("monitoring/incidents.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("monitoring/incidents.tpl.html",
    "<div class=\"col-md-7\" ng-show=\"incidents\">\n" +
    "			\n" +
    "		<div class=\"row\">\n" +
    "			<div class=\"col-sm-6 text-right\"><label>Search: <input ng-model=\"searchText\"></label></div>\n" +
    "		</div>\n" +
    "	\n" +
    "		<table class=\"table table-striped table-hover\">\n" +
    "			<tr>\n" +
    "				<th>Label</th>\n" +
    "				<th>Start Date</th>\n" +
    "				<th>End Date</th>\n" +
    "				<th></th>\n" +
    "				<th></th>\n" +
    "			</tr>\n" +
    "			<!-- <tr ng-repeat=\"profile in status['saved-server-profiles']\"> -->\n" +
    "			<tr ng-repeat=\"incident in incidents.slice(((currentPage-1)*pageSize), ((currentPage)*pageSize)) | filter:searchText track by $index \">\n" +
    "				<td  ng-click=\"profile({id:profile.id})\">{{profile.name}}</td>\n" +
    "				<td>{{profile.startTime | date: 'MMM dd yyyy h:mm a'}}</td>\n" +
    "				<td>{{profile.endTime | date: 'MMM dd yyyy h:mm a'}}</td>\n" +
    "				<td>\n" +
    "					<a ui-sref=\"incident({id:incident.id})\"><span class=\"glyphbutton glyphicons list\" title=\"Review this Incident\"></span></a>\n" +
    "				</td>\n" +
    "				<td>\n" +
    "					<a ui-sref=\"removeIncident({id:incident.id})\"><span class=\"glyphbutton glyphicons remove\" ng-click=\"removeIncident({id:incident.id})\" title=\"Delete this Incident\"></span></a>\n" +
    "				</td>\n" +
    "				\n" +
    "			</tr>\n" +
    "			<tr ng-show=\"incidents.length==0\">\n" +
    "				<td colspan=\"6\">No Incidents Recorded.</td>\n" +
    "			</tr>\n" +
    "		</table>\n" +
    "\n" +
    "		<pagination ng-show=\"status['saved-server-profiles'].length > pageSize\" ng-model=\"currentPage\" total-items=\"status['saved-server-profiles'].length\" items-per-page=\"pageSize\" class=\"pagination-sm\"></pagination>\n" +
    "\n" +
    "</div>");
}]);

angular.module("monitoring/monitoring.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("monitoring/monitoring.tpl.html",
    "<div class=\"panel\">\n" +
    "	<div ui-view></div>\n" +
    "</div>");
}]);

angular.module("monitoring/rules.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("monitoring/rules.tpl.html",
    "<table class=\"table table-striped table-hover table-condensed results\">\n" +
    "	<tr>\n" +
    "		<th>\n" +
    "			<span class=\"glyphicons heart\" style=\"visibility: hidden;\"></span>\n" +
    "			Name\n" +
    "		</th>\n" +
    "		<th>Type</th>\n" +
    "		<th>Threshold</th>\n" +
    "		<th>Logs</th>\n" +
    "		<th>Kills</th>\n" +
    "		<th>Notifies</th>\n" +
    "		<th></th>\n" +
    "	</tr>\n" +
    "	<tr ng-repeat=\"rule in rules\">\n" +
    "		<td ng-class=\"{disabled: !rule.isEnabled}\" ng-click=\"editRule(rule)\">\n" +
    "			<span class=\"glyphicons circle_ok green\" style=\"color:green;\" ng-show = \"rule.isEnabled\"></span>\n" +
    "			<span class=\"glyphicons circle_exclamation_mark red\" style=\"color:red;\" ng-hide = \"rule.isEnabled\"></span>\n" +
    "			{{rule.name}}\n" +
    "		</td>\n" +
    "		<td>{{rule.triggerTypeString}}</td>\n" +
    "		<td>{{rule.triggerLimit}}\n" +
    "		<span ng-switch on=\"rule.triggerType\">\n" +
    "		  <span ng-switch-when=\"ACTIVEREQ\"> </span>\n" +
    "		  <span ng-switch-when=\"\"> </span>\n" +
    "		  <span ng-switch-when=\"MEMORYPCT\">%</span>\n" +
    "		  <span ng-switch-default>Ms</span>\n" +
    "		</span>	\n" +
    "		</td>\n" +
    "		<td>{{rule.isLogResponse}}</td>\n" +
    "		<td>{{rule.isKillResponse}}</td>\n" +
    "		<td>{{rule.isNotifyResponse}}</td>\n" +
    "		<td>\n" +
    "			<span class=\"glyphbutton glyphicons remove\" ng-click=\"removeRule(rule)\" title=\"Delete this rule\"></span>\n" +
    "		</td>\n" +
    "	</tr>\n" +
    "	<tr ng-show=\"rules.length==0\">\n" +
    "		<td colspan=\"10\">No rules defined.</td>\n" +
    "	</tr>\n" +
    "</table>\n" +
    "<button ng-click=\"newRule()\" class=\"btn btn-primary\" type=\"button\">New Rule</button>");
}]);

angular.module("nav.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("nav.tpl.html",
    "<ul  class=\"nav nav-pills nav-stacked\" ng-if=\"isAuthenticated\">\n" +
    "    <li title=\"SeeFusion\">\n" +
    "        <img src=\"SeeFusionInv.png\" alt=\"SeeFusion\" width=\"180\" border=\"0\"/>\n" +
    "    </li>\n" +
    "    <!-- ng-if=\"isAuthenticated\" -->\n" +
    "    <!-- Server --> \n" +
    "    <li ui-sref=\"server.active\" title=\"Server\" ng-class=\"$state.includes('server') ? 'icon_link' : 'white'\">\n" +
    "        <span class=\"glyphicons imac\"></span> <span class=\"navText\">Server</span>\n" +
    "    </li>\n" +
    "    <!-- Dashboard -->\n" +
    "    <li ng-show=\"isEnterprise\" ui-sref=\"dashboard.all\" title=\"Dashboard\" ng-class=\"$state.includes('dashboard') ? 'icon_link' : 'white'\">\n" +
    "        <span class=\"glyphicons more_windows\"></span> <span class=\"navText\">Dashboard</span>\n" +
    "    </li>\n" +
    "    <!-- Counters -->\n" +
    "    <li ui-sref=\"counters\" title=\"Counters\" ng-class=\"$state.includes('counters') ? 'icon_link' : 'white'\">\n" +
    "        <span class=\"glyphicons cardio\"></span> <span class=\"navText\">Counters</span>\n" +
    "    </li>\n" +
    "    <!-- Configuration -->\n" +
    "    <li ui-sref=\"config\" title=\"Configuration\" ng-class=\"$state.includes('config') ? 'icon_link' : 'white'\"><span class=\"glyphicons settings\"></span> <span class=\"navText\">Configuration</span></li>\n" +
    "    <!-- Active Monitoring -->\n" +
    "    <li ui-sref=\"monitoring.rules\" title=\"Active Monitoring\" ng-class=\"$state.includes('monitoring') ? 'icon_link' : 'white'\"><span class=\"glyphicons group\"></span> <span class=\"navText\">Active Monitoring</span></li>\n" +
    "    <!-- Stack -->\n" +
    "    <li ui-sref=\"stack.seestack({threadname:'',requestid:''})\" title=\"Stack\" ng-class=\"$state.includes('stack') ? 'icon_link' : 'white'\"><span class=\"glyphicons show_lines\"></span> <span class=\"navText\">Stack</span></li>\n" +
    "    <!-- Profiling -->\n" +
    "    <li ui-sref=\"profiling\" title=\"Profiling\" ng-class=\"$state.includes('profiling') || $state.includes('profile') ? 'icon_link' : 'white'\"><span class=\"glyphicons record\"></span> <span class=\"navText\">Profiling</span></li>\n" +
    "    <!-- DOS Protection -->\n" +
    "    <li ui-sref=\"dos\" title=\"DOS Protection\" ng-class=\"$state.includes('dos') ? 'icon_link' : 'white'\"><span class=\"glyphicons shield\"></span> <span class=\"navText\">DOS Protection</span></li>\n" +
    "    <!-- Logging -->\n" +
    "    <li ui-sref=\"log\" title=\"Log\" ng-class=\"$state.includes('log') ? 'icon_link' : 'white'\"><span class=\"glyphicons blog\"></span> <span class=\"navText\">Logs</span></li>\n" +
    "    <!-- About -->\n" +
    "    <li ui-sref=\"about\" title=\"About\" ng-class=\"$state.includes('about') ? 'icon_link' : 'white'\"><span class=\"glyphicons circle_question_mark\"></span> <span class=\"navText\">About SeeFusion</span></li>\n" +
    "    \n" +
    "    <li ng-click=\"logout()\" title=\"Logout\" class=\"white\"><span class=\"glyphicons log_out\"></span> <span class=\"navText\">Log Out</span></li>\n" +
    "    <li ng-click=\"toggleNav()\" class=\"white\"><span class=\"glyphicons resize_small\"></span> <span class=\"navText\">Toggle Nav</span></li>\n" +
    "</ul> \n" +
    "");
}]);

angular.module("navExpand.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("navExpand.tpl.html",
    "<ul class=\"nav nav-pills nav-stacked\">\n" +
    "    <li ng-click=\"toggleNav()\" class=\"white\"><span class=\"glyphicons resize_full\"></span></li>\n" +
    "</ul> ");
}]);

angular.module("notifications.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("notifications.tpl.html",
    "<div ng-class=\"['alert', 'alert-'+notification.type]\" ng-repeat=\"notification in notifications.getCurrent()\">\n" +
    "    <button class=\"close\" ng-click=\"removeNotification(notification)\">x</button>\n" +
    "    <span ng-if=\"notification.type === 'success'\" class=\"glyphicon glyphicon-thumbs-up\"></span> \n" +
    "    {{notification.message}}\n" +
    "</div>\n" +
    "");
}]);

angular.module("popup.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("popup.tpl.html",
    "<!-- Modal content-->\n" +
    "<div class=\"modal-content\">\n" +
    "  <div class=\"modal-header\">\n" +
    "    <button type=\"button\" class=\"close\" ng-click=\"cancel()\">&times;</button>\n" +
    "    <h4 class=\"modal-title\"><br/></h4>\n" +
    "  </div>\n" +
    "  <div class=\"modal-body\">\n" +
    "    <p>{{message}}</p>\n" +
    "  </div>\n" +
    "  <div class=\"modal-footer\">\n" +
    "    <button class=\"btn btn-default\" ng-click=\"cancel()\">Close</button>\n" +
    "  </div>\n" +
    "</div>");
}]);

angular.module("problemHeader.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("problemHeader.tpl.html",
    "<div class=\"problemTitle\">{{breadcrumb}}<img src=\"SeeFusion.png\" border=\"0\" class=\"pull-right\"/></div>\n" +
    "");
}]);

angular.module("profiling/profile.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("profiling/profile.tpl.html",
    "<div class=\"container-fluid\">\n" +
    "	<div class=\"row\">\n" +
    "		<div class=\"col-md-5\">\n" +
    "			<div class=\"panel panel-default\">\n" +
    "				<div class=\"panel-heading\">Profile Summary</div>\n" +
    "				<div class=\"panel-body\">\n" +
    "					<div class=\"container-fluid\">\n" +
    "						<div class=\"row\">\n" +
    "							<div class=\"col-sm-5 text-right\">Profile Label:</div>\n" +
    "							<div class=\"col-sm-7\">{{profileInfo.name}}</div>\n" +
    "						</div>\n" +
    "						<div class=\"row\">\n" +
    "							<div class=\"col-sm-5 text-right\">Recorded On:</div>\n" +
    "							<div class=\"col-sm-7\">{{profileInfo.startTick | date: 'MMM dd yyyy h:mm a'}}</div>\n" +
    "						</div>\n" +
    "						<div class=\"row\">\n" +
    "							<div class=\"col-sm-5 text-right\">Profile Duration:</div>\n" +
    "							<div class=\"col-sm-7\">{{profileInfo.actualDurationMs | millSecondsToTimeString}}</div>\n" +
    "						</div>\n" +
    "						<div class=\"row\">\n" +
    "							<div class=\"col-sm-5 text-right\">Recording Interval:</div>\n" +
    "							<div class=\"col-sm-7\">\n" +
    "								<span ng-show=\"{{profileInfo.intervalMs > 999}}\">{{profileInfo.intervalMs | millSecondsToTimeString}}</span>\n" +
    "								<span ng-hide=\"{{profileInfo.intervalMs > 999}}\">{{profileInfo.intervalMs}} ms</span>\n" +
    "							</div>\n" +
    "						</div>\n" +
    "						<div class=\"row\">\n" +
    "							<div class=\"col-sm-5 text-right\">Total Captured:</div>\n" +
    "							<div class=\"col-sm-7\">{{profileInfo.snapshotCount}}</div>\n" +
    "						</div>\n" +
    "					</div> <!-- container -->\n" +
    "				</div> <!-- panel body -->\n" +
    "			</div> <!-- panel -->\n" +
    "\n" +
    "			<div class=\"panel panel-default\">\n" +
    "				<div class=\"panel-heading\">Recorded Traces</div>\n" +
    "				<div class=\"panel-body scrollHorizontal\">\n" +
    "					<div ng-repeat=\"profile in profiles\">\n" +
    "						<a ng-click=\"showTrace(profile)\">{{profile.count}}:{{profile.description}}</a>\n" +
    "					</div>\n" +
    "					<div ng-hide=\"profiles\">None Recorded</div>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "		</div>\n" +
    "\n" +
    "		<div class=\"col-md-7\">\n" +
    "			<div class=\"panel panel-default\">\n" +
    "				<div class=\"panel-heading\">Trace Details</div>\n" +
    "				<div class=\"panel-body scrollHorizontal\" ng-show=\"stacktrace\">\n" +
    "					<p><strong>{{stacktrace.description}}</strong><br/>Seen {{stacktrace.count}} times</p>\n" +
    "					<div ng-repeat=\"analysis in stacktrace.analysis\" style=\"margin-top:2em;\">\n" +
    "						<div><a href=\"#\" onClick=\"return false;\" ng-click=\"showtrace(stacktrace)\">Seen {{analysis.count}} times: {{analysis.description}}</a></div>\n" +
    "						<div class=\"seethread\" ng-repeat=\"thread in analysis.threads\">\n" +
    "							<div>\n" +
    "								<abbr ng-show=\"thread.ref\" class=\"threadName {{thread.importance}}\" title=\"{{infos[thread.ref].description}}\">Example {{$index+1}} (Seen {{thread.count}} times):</abbr>\n" +
    "								<span ng-hide=\"thread.ref\" class=\"threadName {{thread.importance}}\">{{thread.name}}</span>\n" +
    "							</div>\n" +
    "							<div ng-repeat=\"method in thread.methods\" class=\"method\">\n" +
    "								<span ng-show=\"method.package\">\n" +
    "									<abbr class=\"{{seestack.infos[method.packageInfo].importance}}\" title=\"{{infos[method.packageInfo].description}}\">{{method.package}}</abbr>\n" +
    "									<abbr ng-show=\"method.methodInfo\" class=\"{{method.importance}}\" title=\"{{infos[method.methodInfo].description}}\">{{method.rawLineAfterPackage}}</abbr>\n" +
    "									<span ng-hide=\"method.methodInfo\" class=\"{{method.importance}}\">{{method.rawLineAfterPackage}}</span>\n" +
    "								</span>\n" +
    "								<span ng-hide=\"method.package\" class=\"{{method.importance}}\">\n" +
    "									<abbr ng-show=\"method.methodInfo\" class=\"{{method.importance}}\" title=\"{{infos[method.methodInfo].description}}\">{{method.rawLine}}</abbr>\n" +
    "									<span ng-hide=\"method.methodInfo\" class=\"{{method.importance}}\">{{method.rawLine}}</span>\n" +
    "								</span>\n" +
    "							</div>\n" +
    "						</div>\n" +
    "					</div>\n" +
    "				</div>\n" +
    "				<div class=\"panel-body\" ng-hide=\"stacktrace\">\n" +
    "					None Recorded\n" +
    "				</div>\n" +
    "			</div>\n" +
    "		</div>\n" +
    "	</div>\n" +
    "</div>");
}]);

angular.module("profiling/profiling.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("profiling/profiling.tpl.html",
    "<div ng-show=\"initialLoading\" class=\"alert relative alert-info\"><img src=\"/img/spinner.gif\"> Loading Profile Data...</div>\n" +
    "<div class=\"container-fluid\" ng-hide=\"initialLoading\">\n" +
    "	<div class=\"row\">\n" +
    "		<div class=\"col-md-5\">\n" +
    "			\n" +
    "			<form class=\"form-horizontal\" name=\"newProfileForm\" novalidate> \n" +
    "\n" +
    "			<div class=\"col-md-11 nopadding panel panel-default\">\n" +
    "				<div class=\"panel-heading\">\n" +
    "					<h3 class=\"panel-title\">\n" +
    "						<span ng-hide=\"active.id\">Record a New Profile:</span>\n" +
    "						<span ng-show=\"active.id\"><img src=\"/img/spinner_bar.gif\" alt=\"Recording\"> Recording...</span>\n" +
    "					</h3>\n" +
    "				</div>\n" +
    "\n" +
    "				<div class=\"content panel-body\" id=\"active-profiler-status\">\n" +
    "\n" +
    "					<div ng-hide=\"active.id\"> \n" +
    "					<!-- profile start form, hidden when profile is recording -->\n" +
    "\n" +
    "						<div class=\"form-group\" ng-class=\"{'has-error' : newProfileForm.name.$dirty && newProfileForm.name.$invalid}\">\n" +
    "							<label class=\"col-sm-2 control-label\" for=\"inputName\">Label</label>\n" +
    "							<div class=\"col-sm-8\">\n" +
    "								<input type=\"text\" id=\"name\" name=\"name\" placeholder=\"My Profile\" ng-model=\"newProfile.name\" ng-required ng-pattern=\"/^[a-zA-Z0-9 ]*$/\" class=\"form-control\" />\n" +
    "								<div role=\"alert\" class=\"errorHighlight hint\" ng-show=\"newProfileForm.name.$dirty\">\n" +
    "					      		<span  ng-show=\"newProfileForm.name.$error.required\">Please enter a label for this profile</span>\n" +
    "					      		<span  ng-show=\"newProfileForm.name.$error.pattern\">Labels can only contain letters, numbers and spaces</span>\n" +
    "					    		</div>\n" +
    "							</div>\n" +
    "						</div>\n" +
    "\n" +
    "						<div class=\"form-group\"  ng-class=\"{'has-error' : newProfileForm.scheduledDurationMin.$invalid && startClicked}\">\n" +
    "							<label class=\"col-sm-2 control-label\" for=\"scheduledDurationMin\">Duration</label>\n" +
    "							<div class=\"col-sm-4\">\n" +
    "								<select id=\"scheduledDurationMin\" name=\"scheduledDurationMin\" ng-model=\"newProfile.scheduledDurationMin\" class=\"input-mini form-control\" required  ng-options=\"d as (d*60000 | durationString) for d in durationOptions\" ng-disabled=\"!newProfile.name\" ng-change=\"getIntervalOptions(newProfile.scheduledDurationMin)\"></select>\n" +
    "\n" +
    "								<div role=\"alert\" class=\"errorHighlight hint\" ng-show=\"startClicked\">\n" +
    "					      			<span ng-show=\"newProfileForm.scheduledDurationMin.$invalid\">Choose a duration</span>\n" +
    "					    		</div>\n" +
    "							</div>\n" +
    "							<div class=\"col-sm-4 hint\">\n" +
    "								How long the profile will record \n" +
    "							</div>\n" +
    "						</div>\n" +
    "\n" +
    "						<div class=\"form-group\" ng-class=\"{'has-error' : newProfileForm.interval.$invalid  && startClicked}\">\n" +
    "							<label class=\"col-sm-2 control-label\" for=\"interval\">Interval</label>\n" +
    "							<div class=\"col-sm-4\">\n" +
    "								<select id=\"interval\" name=\"interval\" ng-model=\"newProfile.interval\" class=\"input-mini form-control\" required  ng-options=\"i as (i | durationString) for i in durationIntervalOptions\" ng-disabled=\"!newProfile.scheduledDurationMin || !durationIntervalOptions.length\"></select>\n" +
    "\n" +
    "								<div role=\"alert\" class=\"errorHighlight hint\" ng-show=\"startClicked\">\n" +
    "					      			<span ng-show=\"newProfileForm.interval.$invalid\">Choose an Interval</span>\n" +
    "					    		</div>\n" +
    "							</div>\n" +
    "							<div class=\"col-sm-4 hint\">\n" +
    "								How often a snapshot is recorded\n" +
    "							</div>\n" +
    "						</div>\n" +
    "\n" +
    "						<div class=\"form-group\">\n" +
    "							<label class=\"col-sm-2 control-label\"> </label>\n" +
    "							<div class=\"col-sm-8\">\n" +
    "								<button class=\"btn btn-primary\" href=\"#\" onClick=\"return false;\" ng-click=\"startProfiling()\" ng-hide=\"starting\" ng-disabled=\"newProfileForm.$invalid\">Start Profiler</button> \n" +
    "								<button class=\"btn btn-warning\" href=\"#\" ng-show=\"starting\"><img src=\"/img/spinner_circle.gif\" alt=\"Starting\"> Starting</button> \n" +
    "							</div>\n" +
    "						</div>\n" +
    "					</div> <!-- profile form div -->\n" +
    "		\n" +
    "					<div ng-show=\"active.id\">\n" +
    "						\n" +
    "						<h4>{{active.name}}</h4>\n" +
    "						<div class=\"em\">{{active.message}}</div>\n" +
    "						<progressbar value=\"timePercent\" title=\"Time Remaining\" animated=\"true\"></progressbar>\n" +
    "\n" +
    "						<div class=\"col-sm-6 text-center\">\n" +
    "							<div class=\"panel panel-default\">\n" +
    "								<div class=\"panel-heading\"><b>Time Left</b></div>\n" +
    "								<div class=\"panel-body\"><h3 class=\"nopadding\">{{activeTimeRemaining  | millSecondsToTimerString}}</h3></div>\n" +
    "							</div>\n" +
    "						</div>\n" +
    "						<div class=\"col-sm-6 text-center\">\n" +
    "							<div class=\"panel panel-default\">\n" +
    "								<div class=\"panel-heading\"><b>Recorded</b></div>\n" +
    "								<div class=\"panel-body\"><h3 class=\"nopadding\">{{active.snapshotCount}}</h3></div>\n" +
    "							</div>\n" +
    "						</div>\n" +
    "\n" +
    "						<table border=\"0\" class=\"padded\">\n" +
    "						<tr>\n" +
    "							<td class=\"listLabel\">Recording Started: </td>\n" +
    "							<td>{{active.startTick | date: 'MMM dd, yyyy @ h:mm a'}}</td>\n" +
    "						</tr>\n" +
    "						<tr>\n" +
    "							<td class=\"listLabel\">Recording Interval: </td>\n" +
    "							<td>{{active.intervalMs  | millSecondsToReadable}}</td>\n" +
    "						</tr>\n" +
    "						<tr>\n" +
    "							<td class=\"listLabel\">Scheduled Duration:  </td>\n" +
    "							<td>{{active.scheduledDurationMs | millSecondsToTimeString}}</td>\n" +
    "						</tr>\n" +
    "						</table>\n" +
    "						<p></p>\n" +
    "						<button class=\"btn btn-danger\" href=\"#\" onClick=\"return false;\" ng-click=\"stopProfiling()\" ng-hide=\"stopping\">Stop Profiler</button>\n" +
    "						<button class=\"btn btn-warning\" href=\"#\" ng-show=\"stopping\"><img src=\"/img/spinner_circle.gif\" alt=\"Stopping\"> Stopping</button> \n" +
    "							\n" +
    "					</div> <!-- profile recording div -->\n" +
    "				</div> <!-- left panel body -->\n" +
    "			</div> <!-- left panel -->\n" +
    "			</form>\n" +
    "		</div>\n" +
    "		\n" +
    "		<div class=\"col-md-7\" ng-show=\"status['saved']\">\n" +
    "			\n" +
    "				<div class=\"row\">\n" +
    "					<div class=\"col-sm-6\"><h2 class=\"subtitle\">Saved Profiles</h2></div>\n" +
    "					<div class=\"col-sm-6 text-right\"><label>Search: <input ng-model=\"searchText\"></label></div>\n" +
    "				</div>\n" +
    "			\n" +
    "				<table class=\"table table-striped table-hover\">\n" +
    "					<tr>\n" +
    "						<th>Label</th>\n" +
    "						<th>Record Date</th>\n" +
    "						<th>Duration</th>\n" +
    "						<th>Snapshots</th>\n" +
    "						<th></th>\n" +
    "						<th></th>\n" +
    "					</tr>\n" +
    "					<tr ng-repeat=\"profile in filtered = status['saved'] | filter:searchText | startFrom:(currentPage-1)*pageSize | limitTo:pageSize\">\n" +
    "						<td>{{profile.name}}</td>\n" +
    "						<td>{{profile.startTick | date: 'MMM dd yyyy h:mm a'}}</td>\n" +
    "						<td>{{profile.actualDurationMs | millSecondsToTimeString}}</td>\n" +
    "						<td>{{profile.snapshotCount}}</td>\n" +
    "						<td>\n" +
    "							<a ui-sref=\"profile({id:profile.id})\"><span class=\"glyphbutton glyphicons list\" title=\"Review this Profile\"></span></a>\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							<span class=\"glyphbutton glyphicons remove linkish\" ng-click=\"removeProfile(profile)\" title=\"Delete this Profile\"></span>\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "					<tr ng-show=\"profiles.length==0\">\n" +
    "						<td colspan=\"6\"><div class=\"alert relative alert-info\" role=\"alert\">No Saved Profiles.</div></td>\n" +
    "					</tr>\n" +
    "				</table>\n" +
    "\n" +
    "				<pagination ng-model=\"currentPage\" total-items=\"totalItems\" items-per-page=\"pageSize\" class=\"pagination-sm\" ng-show=\"noOfPages > 1\"></pagination>\n" +
    "\n" +
    "				<!--  <pagination page=\"currentPage\" max-size=\"noOfPages\" total-items=\"totalItems\" items-per-page=\"pageSize\"></pagination> -->\n" +
    "		</div>\n" +
    "	</div>\n" +
    "</div>");
}]);

angular.module("server/active.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("server/active.tpl.html",
    "<div class=\"requestList\" ng-hide=\"lookingRequest.on\">\n" +
    "	<table class=\"table table-striped table-hover table-condensed results\">\n" +
    "		<thead>\n" +
    "			<tr>\n" +
    "				<th ng-click=\"sortType = 'url'; sortReverse = !sortReverse\">\n" +
    "		            Request \n" +
    "		            <span ng-show=\"sortType == 'url' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'url' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<!-- <th ng-click=\"sortType = 'completed'; sortReverse = !sortReverse\">\n" +
    "		            Completed \n" +
    "		            <span ng-show=\"sortType == 'completed' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'completed' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th> -->\n" +
    "				<th ng-click=\"sortType = 'ip'; sortReverse = !sortReverse\">\n" +
    "		            From IP\n" +
    "		            <span ng-show=\"sortType == 'ip' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'ip' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'responseCode'; sortReverse = !sortReverse\">\n" +
    "		            Response\n" +
    "		            <span ng-show=\"sortType == 'responseCode' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'responseCode' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'time'; sortReverse = !sortReverse\">\n" +
    "		            Elapsed\n" +
    "		            <span ng-show=\"sortType == 'time' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'time' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'queryCount'; sortReverse = !sortReverse\">\n" +
    "		            Queries\n" +
    "		            <span ng-show=\"sortType == 'queryCount' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'queryCount' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'queryTime'; sortReverse = !sortReverse\">\n" +
    "		            QryTime\n" +
    "		            <span ng-show=\"sortType == 'queryTime' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'queryTime' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'longQueryElapsed'; sortReverse = !sortReverse\">\n" +
    "		            LongQry\n" +
    "		            <span ng-show=\"sortType == 'longQueryElapsed' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'longQueryElapsed' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'longQueryRows'; sortReverse = !sortReverse\">\n" +
    "		            Rows\n" +
    "		            <span ng-show=\"sortType == 'longQueryRows' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'longQueryRows' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'longQuerySql'; sortReverse = !sortReverse\">\n" +
    "		            SQL\n" +
    "		            <span ng-show=\"sortType == 'longQuerySql' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'longQuerySql' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "			</tr>\n" +
    "		</thead>\n" +
    "		<tbody>\n" +
    "			<tr ng-show=\"pages.length==0 && !busy\">\n" +
    "				<td colspan=\"10\">No active requests</td>\n" +
    "			</tr>\n" +
    "			<tr ng-repeat=\"page in pages | orderBy:sortType:sortReverse\" ng-class=\"{highlighted:page.pid === detailRequestNumber}\">\n" +
    "				<td>\n" +
    "					<!-- <button ng-click=\"getStack(page)\" type=\"button\" href=\"#\" onClick=\"return false;\"><img src=\"images/stack.gif\" border=0></button> -->\n" +
    "					<!-- <button ng-click=\"kill(page)\" ng-show=\"canKill\" type=\"button\" href=\"#\" onClick=\"return false;\"><img src=\"images/stop.png\" border=0></button> -->\n" +
    "					<button ng-click=\"getStack(page)\" title=\"View Stack\"><span class=\"glyphicons show_lines blue\"></span></button>\n" +
    "					<button ng-click=\"kill(page)\" ng-if=\"canKill\"  title=\"Kill Request\"><span class=\"glyphicons stop red\"></span></button>\n" +
    "					<button ng-click=\"open(page)\" ng-if=\"clickableURLs\" title=\"Open this URL\"><span class=\"glyphicons share green\"></span></button>\n" +
    "					<span ng-click=\"requestDetails(page)\">{{page.url | limitTo : 70}}</span><span ng-show=\"page.url.length > 70\">...</span>\n" +
    "				</td>\n" +
    "				<!-- <td ng-click=\"requestDetails(page)\">{{page.completed | date:'short'}}</td> -->\n" +
    "				<td nowrap>\n" +
    "					<span ng-show=\"dosEnabled\" class=\"glyphicons ban\" ng-click=\"addBlock(page.ip)\" title=\"Block {{page.ip}}\"></span>\n" +
    "					<span ng-show=\"clickableIPURL\" class=\"glyphicons globe\" ng-click=\"open(clickableIPURL + page.ip)\" title=\"IP info\"></span>\n" +
    "					{{page.ip}}\n" +
    "				</td>\n" +
    "				<td ng-click=\"requestDetails(page)\">{{page.responseCode}}<span ng-show=\"page.timeToFirstByte > 0\">@{{page.timeToFirstByte}}ms</span></td>\n" +
    "				<td ng-click=\"requestDetails(page)\">{{page.time}}ms</td>\n" +
    "				<td ng-click=\"requestDetails(page)\">{{page.queryCount}}</td>\n" +
    "				<td ng-click=\"requestDetails(page)\">{{page.queryTime}}ms</td>\n" +
    "				<td ng-click=\"requestDetails(page)\">{{page.longQueryElapsed}}ms</td>\n" +
    "				<td ng-click=\"requestDetails(page)\">{{page.longQueryRows}}</td>\n" +
    "				<td><span ng-click=\"requestDetails(page)\">{{page.longQuerySql | limitTo : 70}}</span><span ng-show=\"page.longQuerySql.length > 70\">...</span></td>\n" +
    "			</tr>\n" +
    "		</tbody>\n" +
    "	</table>\n" +
    "</div>\n" +
    "");
}]);

angular.module("server/chart-modal.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("server/chart-modal.tpl.html",
    "<div class=\"modal-header\">\n" +
    "	<h3 class=\"modal-title\">{{charts.title}}</h3>\n" +
    "</div>\n" +
    "<div class=\"modal-body chart\">\n" +
    "	<div ng-if=\"charts.data.length\">\n" +
    "		<nvd3-line-chart\n" +
    "		id=\"chartModal\"\n" +
    "		data=\"charts.data\"\n" +
    "		showXAxis=\"true\"\n" +
    "		showYAxis=\"true\"\n" +
    "		tooltips=\"true\"\n" +
    "		interactive=\"true\"\n" +
    "		showLegend=\"true\"\n" +
    "		xAxisTickFormat=\"chartDateFormat()\"\n" +
    "		height=\"{{charts.height}}\">\n" +
    "		</nvd3-line-chart>\n" +
    "	</div>\n" +
    "</div>\n" +
    "\n" +
    "<div class=\"modal-footer\">\n" +
    "	<button class=\"btn btn-default\" ng-click=\"cancel()\">Close</button>\n" +
    "</div>\n" +
    "");
}]);

angular.module("server/chart-modal2.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("server/chart-modal2.tpl.html",
    "<div class=\"modal-header\">\n" +
    "	<h3 class=\"modal-title\">{{modal.title}}</h3>\n" +
    "</div>\n" +
    "	<div class=\"modal-body chart\">\n" +
    "		<nvd3-line-chart\n" +
    "		id=\"chartModal\"\n" +
    "		data=\"charts[modal.chart]\"\n" +
    "		showXAxis=\"true\"\n" +
    "		showYAxis=\"true\"\n" +
    "		tooltips=\"true\"\n" +
    "		interactive=\"true\"\n" +
    "		showLegend=\"true\"\n" +
    "		xAxisTickFormat=\"chartDateFormat()\">\n" +
    "		</nvd3-line-chart>\n" +
    "	</div>\n" +
    "		\n" +
    "</div>\n" +
    "\n" +
    "<div class=\"modal-footer\">\n" +
    "	<button class=\"btn btn-default\" ng-click=\"modalCancel()\">Close</button>\n" +
    "</div>");
}]);

angular.module("server/recent.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("server/recent.tpl.html",
    "<div class=\"requestList\" ng-hide=\"lookingRequest.on\">\n" +
    "	<table class=\"table table-striped table-hover table-condensed results\">\n" +
    "		<thead>\n" +
    "			<tr>\n" +
    "				<th ng-click=\"sortType = 'url'; sortReverse = !sortReverse\">\n" +
    "		            Request\n" +
    "		            <span ng-show=\"sortType == 'url' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'url' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'completed'; sortReverse = !sortReverse\">\n" +
    "		            Completed \n" +
    "		            <span ng-show=\"sortType == 'completed' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'completed' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'ip'; sortReverse = !sortReverse\">\n" +
    "		            From IP\n" +
    "		            <span ng-show=\"sortType == 'ip' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'ip' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'responseCode'; sortReverse = !sortReverse\">\n" +
    "		            Response\n" +
    "		            <span ng-show=\"sortType == 'responseCode' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'responseCode' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'time'; sortReverse = !sortReverse\">\n" +
    "		            Elapsed\n" +
    "		            <span ng-show=\"sortType == 'time' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'time' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'queryCount'; sortReverse = !sortReverse\">\n" +
    "		            Queries\n" +
    "		            <span ng-show=\"sortType == 'queryCount' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'queryCount' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'queryTime'; sortReverse = !sortReverse\">\n" +
    "		            QryTime\n" +
    "		            <span ng-show=\"sortType == 'queryTime' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'queryTime' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'longQueryElapsed'; sortReverse = !sortReverse\">\n" +
    "		            LongQry\n" +
    "		            <span ng-show=\"sortType == 'longQueryElapsed' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'longQueryElapsed' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'longQueryRows'; sortReverse = !sortReverse\">\n" +
    "		            Rows\n" +
    "		            <span ng-show=\"sortType == 'longQueryRows' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'longQueryRows' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'longQuerySql'; sortReverse = !sortReverse\"> \n" +
    "		            SQL\n" +
    "		            <span ng-show=\"sortType == 'longQuerySql' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'longQuerySql' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "			</tr>\n" +
    "		</thead>\n" +
    "		<tbody>\n" +
    "			<tr ng-show=\"pages.length==0 && !busy\"> \n" +
    "				<td colspan=\"10\">No recent requests.</td>\n" +
    "			</tr>\n" +
    "			<!-- <tr><td colspan=8>{{pages[0] | json}}</td></tr> -->\n" +
    "			<tr class=\"requestRow\" ng-repeat=\"page in pages | orderBy:sortType:sortReverse\" ng-class=\"{highlighted:page.pid === detailRequestNumber}\">\n" +
    "				<td><button ng-click=\"open(page)\"  ng-if=\"clickableURLs\" title=\"Open this URL\"><span class=\"glyphicons share green\"></span></button> <span ng-click=\"requestDetails(page)\">{{page.url | limitTo : 70}}</span><span ng-show=\"page.url.length > 70\">...</span></td>\n" +
    "				<td><span ng-show=\"displayRelativeTimes\">{{page.completedAgoMs | millSecondsToTimeString}} ago</span><span ng-show=\"!displayRelativeTimes\">{{page.completed | date:'mediumTime' | lowercase}}</span></td>\n" +
    "				<td nowrap>\n" +
    "					<span ng-show=\"dosEnabled\" class=\"glyphicons ban\" ng-click=\"addBlock(page.ip)\" title=\"Block {{page.ip}}\"></span>\n" +
    "					<span ng-show=\"clickableIPURL\" class=\"glyphicons globe\" ng-click=\"open(clickableIPURL + page.ip)\" title=\"IP info\"></span>\n" +
    "					{{page.ip}}\n" +
    "				</td> \n" +
    "				<td>{{page.responseCode}}<span ng-show=\"page.timeToFirstByte > 0\">@{{page.timeToFirstByte}}ms</span></td>\n" +
    "				<td>{{page.time}}ms</td>\n" +
    "				<td>{{page.queryCount}}</td>\n" +
    "				<td>{{page.queryTime}}ms</td>\n" +
    "				<td>{{page.longQueryElapsed}}ms</td>\n" +
    "				<td>{{page.longQueryRows}}</td>\n" +
    "				<td><span ng-click=\"requestDetails(page)\">{{page.longQuerySql | limitTo : 70}}</span><span ng-show=\"page.longQuerySql.length > 70\">...</span></td>\n" +
    "			</tr> \n" +
    "		</tbody>\n" +
    "	</table>\n" +
    "</div> ");
}]);

angular.module("server/requestDetails.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("server/requestDetails.tpl.html",
    "<div ng-hide=\"lookingRequest.info\" class=\"alert relative alert-info\" role=\"alert\">\n" +
    "  No request details available.\n" +
    "</div>\n" +
    "\n" +
    "<div ng-show=\"lookingRequest.info\" class=\"requestDetail\">\n" +
    "\n" +
    "<table class=\"table table-striped table-hover\">\n" +
    "	<tbody>\n" +
    "		<tr>\n" +
    "			<td class=\"listLabel\">Request:</td>\n" +
    "			<td width=\"90%\"><button ng-click=\"open(lookingRequest.info)\"><span class=\"glyphicons share green\"></span></button> {{lookingRequest.info.url}}</td>\n" +
    "		</tr>\n" +
    "		<tr>\n" +
    "			<td class=\"listLabel\">Thread:</td>\n" +
    "			<td width=\"90%\">{{lookingRequest.info.threadName}} (Req #{{lookingRequest.info.pid}})</td>\n" +
    "		</tr>\n" +
    "		<tr>\n" +
    "			<td class=\"listLabel\">Completed:</td>\n" +
    "			<td width=\"90%\">{{lookingRequest.info.completed | date:'short'}} ({{lookingRequest.info.completedAgoMs | millSecondsToTimeString}} ago)</td>\n" +
    "		</tr>\n" +
    "		<tr>\n" +
    "			<td class=\"listLabel\" >Source IP:</td>\n" +
    "			<td width=\"90%\">{{lookingRequest.info.ip}} \n" +
    "				<span ng-show=\"dosEnabled\" class=\"glyphicons ban\" ng-click=\"addBlock(lookingRequest.info.ip)\" title=\"Block {{page.ip}}\"></span>\n" +
    "				<span ng-show=\"clickableIPURL\" class=\"glyphicons globe\" ng-click=\"open(clickableIPURL + lookingRequest.info.ip)\" title=\"IP info\"></span>\n" +
    "			</td>\n" +
    "		</tr>\n" +
    "		<tr>\n" +
    "			<td class=\"listLabel\" text-right>HTTP Response Code:</td>\n" +
    "			<td width=\"90%\">{{lookingRequest.info.responseCode}} ({{lookingRequest.info.method}}, HTTP<span ng-if=\"lookingRequest.info.isSecure\">S</span>)</td>\n" +
    "		</tr>\n" +
    "		<tr>\n" +
    "			<td class=\"listLabel\">Time to First Byte:</td>\n" +
    "			<td width=\"90%\"><span ng-if=\"lookingRequest.info.timeToFirstByte\">{{lookingRequest.info.timeToFirstByte}} ms</span></td>\n" +
    "		</tr>\n" +
    "		<tr>\n" +
    "			<td class=\"listLabel\">Total Time:</td>\n" +
    "			<td width=\"90%\">{{lookingRequest.info.time}} ms</td>\n" +
    "		</tr>\n" +
    "		<tr ng-show=\"lookingRequest.info.trace\">\n" +
    "			<td class=\"listLabel\">Trace:</td>\n" +
    "			<td width=\"90%\">{{lookingRequest.info.trace}}</td>\n" +
    "		</tr>\n" +
    "		<tr>\n" +
    "			<td class=\"listLabel\">Completed Queries:</td>\n" +
    "			<td width=\"90%\">\n" +
    "				<span ng-show=\"lookingRequest.info.queryCount\">{{lookingRequest.info.queryCount}} ({{lookingRequest.info.queryTime}} ms)</span>\n" +
    "				<span ng-hide=\"lookingRequest.info.queryCount\">No Queries Logged</span>\n" +
    "			</td>\n" +
    "		</tr>\n" +
    "		<tr ng-show=\"lookingRequest.info.queryCount\">\n" +
    "			<td class=\"listLabel\">Long Query:</td>\n" +
    "			<td width=\"90%\">\n" +
    "				<p ng-show=\"lookingRequest.info.longQueryActive\">STILL ACTIVE</p>\n" +
    "				{{lookingRequest.info.longQueryElapsed}} ms,  {{lookingRequest.info.longQueryRows}} rows\n" +
    "				<span ng-show=\"lookingRequest.info.longQueryActive\">(so far)</span>\n" +
    "				<span ng-show=\"lookingRequest.info.longQuerySql\">:</span>\n" +
    "				<div ng-show=\"lookingRequest.info.longQuerySql\">\n" +
    "					<pre>{{lookingRequest.info.longQuerySql}}</pre>\n" +
    "				</div>\n" +
    "			</td>\n" +
    "		</tr>\n" +
    "	</tbody>\n" +
    "</table>\n" +
    "");
}]);

angular.module("server/server.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("server/server.tpl.html",
    "<div class=\"content\">\n" +
    "	<div>\n" +
    "		<ul class=\"nav nav-tabs\">\n" +
    "		  <li ng-class=\"{active:$state.includes('server.active') && !lookingRequest.on}\"><a ui-sref=\"server.active\" ng-click=\"closeDetail()\">Active Requests</a></li>\n" +
    "		  <li ng-class=\"{active:$state.includes('server.recent') && !lookingRequest.on}\"><a ui-sref=\"server.recent\" ng-click=\"closeDetail()\">Recent Requests</a></li>\n" +
    "		  <li ng-class=\"{active:$state.includes('server.slow') && !lookingRequest.on}\"><a ui-sref=\"server.slow\" ng-click=\"closeDetail()\">Slow Requests</a></li>\n" +
    "		  <li ng-show=\"lookingRequest.on\" ng-class=\"{active:lookingRequest.on}\"><a name=\"details\">Request Details <span class=\"glyphicons remove leftMargin\" ng-click=\"closeDetail();\"></span></a></li>\n" +
    "		</ul>\n" +
    "		<!-- <div ng-show=\"ititialLoading\" class=\"alert relative alert-info\"><img src=\"/img/spinner.gif\"> Loading Request Data...</div> -->\n" +
    "		<div class=\"push-down\" ui-view></div> \n" +
    "	</div>\n" +
    "</div>\n" +
    "\n" +
    "<div class=\"push-down\" ng-hide=\"lookingRequest.on\">\n" +
    "\n" +
    "	<div id=\"pagesChart\" class=\"col-md-4\">\n" +
    "		<div class=\"panel panel-default\">\n" +
    "			<div class=\"panel-heading\" ng-click=\"expandChart('pages','Pages')\">\n" +
    "				<div class=\"pull-right glyphicons resize_full\"></div>\n" +
    "				<h3 class=\"panel-title\">Pages</h3>\n" +
    "			</div>\n" +
    "			<div class=\"panel-body chart\">\n" +
    "				<div ng-if=\"charts.pages.length\">\n" +
    "					<nvd3-line-chart\n" +
    "					id=\"chartPages\"\n" +
    "					data=\"charts.pages\"\n" +
    "					showXAxis=\"true\"\n" +
    "					showYAxis=\"true\"\n" +
    "					tooltips=\"true\"\n" +
    "					interactive=\"true\"\n" +
    "					showLegend=\"true\"\n" +
    "					xAxisTickFormat=\"chartDateFormat()\">\n" +
    "					</nvd3-line-chart>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "		</div>\n" +
    "	</div>\n" +
    "	<div id=\"queriesChart\" class=\"col-md-4\">\n" +
    "		<div class=\"panel panel-default\">\n" +
    "			<div class=\"panel-heading\" ng-click=\"expandChart('queries','Queries')\">\n" +
    "				<div class=\"pull-right glyphicons resize_full\"></div>\n" +
    "				<h3 class=\"panel-title\">Queries</h3>\n" +
    "			</div>\n" +
    "			<div class=\"panel-body chart\">\n" +
    "				<div ng-if=\"charts.queries.length\">\n" +
    "					<nvd3-line-chart\n" +
    "					id=\"chartQueries\"\n" +
    "					data=\"charts.queries\"\n" +
    "					showXAxis=\"true\"\n" +
    "					showYAxis=\"true\"\n" +
    "					tooltips=\"true\"\n" +
    "					interactive=\"true\"\n" +
    "					showLegend=\"true\"\n" +
    "					xAxisTickFormat=\"chartDateFormat()\">\n" +
    "					</nvd3-line-chart>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "		</div>\n" +
    "	</div>\n" +
    "	<div id=\"memoryChart\" class=\"col-md-4\">\n" +
    "		<div class=\"panel panel-default\">\n" +
    "			<div class=\"panel-heading\" ng-click=\"expandChart('memory','Memory')\">\n" +
    "				<div class=\"pull-right glyphicons resize_full\"></div>\n" +
    "				<h3 class=\"panel-title\">Memory</h3>\n" +
    "			</div>\n" +
    "			<div class=\"panel-body chart\">\n" +
    "				<div ng-if=\"charts.memory.length\">\n" +
    "					<nvd3-line-chart\n" +
    "					id=\"chartMemory\"\n" +
    "					data=\"charts.memory\"\n" +
    "					showXAxis=\"true\"\n" +
    "					showYAxis=\"true\"\n" +
    "					tooltips=\"true\"\n" +
    "					interactive=\"true\"\n" +
    "					showLegend=\"true\"\n" +
    "					xAxisTickFormat=\"chartDateFormat()\">\n" +
    "					</nvd3-line-chart>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "		</div>\n" +
    "	</div>\n" +
    "</div>\n" +
    "\n" +
    "<div class=\"push-down\" ng-show=\"lookingRequest.on\" ng-include=\"'server/requestDetails.tpl.html'\"></div>\n" +
    "");
}]);

angular.module("server/slow.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("server/slow.tpl.html",
    "<div class=\"requestList\" ng-hide=\"lookingRequest.on\">\n" +
    "	<table class=\"table table-striped table-hover table-condensed results\">\n" +
    "		<thead>\n" +
    "			<tr>\n" +
    "				<th ng-click=\"sortType = 'url'; sortReverse = !sortReverse\">\n" +
    "		            Request \n" +
    "		            <span ng-show=\"sortType == 'url' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'url' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'completed'; sortReverse = !sortReverse\">\n" +
    "		            Completed \n" +
    "		            <span ng-show=\"sortType == 'completed' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'completed' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'ip'; sortReverse = !sortReverse\">\n" +
    "		            From IP\n" +
    "		            <span ng-show=\"sortType == 'ip' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'ip' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'responseCode'; sortReverse = !sortReverse\">\n" +
    "		            Response\n" +
    "		            <span ng-show=\"sortType == 'responseCode' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'responseCode' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'time'; sortReverse = !sortReverse\">\n" +
    "		            Elapsed\n" +
    "		            <span ng-show=\"sortType == 'time' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'time' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'queryCount'; sortReverse = !sortReverse\">\n" +
    "		            Queries\n" +
    "		            <span ng-show=\"sortType == 'queryCount' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'queryCount' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'queryTime'; sortReverse = !sortReverse\">\n" +
    "		            QryTime\n" +
    "		            <span ng-show=\"sortType == 'queryTime' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'queryTime' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'longQueryElapsed'; sortReverse = !sortReverse\">\n" +
    "		            LongQry\n" +
    "		            <span ng-show=\"sortType == 'longQueryElapsed' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'longQueryElapsed' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'longQueryRows'; sortReverse = !sortReverse\">\n" +
    "		            Rows\n" +
    "		            <span ng-show=\"sortType == 'longQueryRows' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'longQueryRows' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'longQuerySql'; sortReverse = !sortReverse\">\n" +
    "		            SQL\n" +
    "		            <span ng-show=\"sortType == 'longQuerySql' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'longQuerySql' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "			</tr>\n" +
    "		</thead>\n" +
    "		<tbody>\n" +
    "			<tr ng-show=\"pages.length==0 && !busy\">\n" +
    "				<td colspan=\"10\">No slow requests.</td>\n" +
    "			</tr>\n" +
    "			<tr ng-repeat=\"page in pages | orderBy:sortType:sortReverse\"  ng-class=\"{highlighted:page.requestNumber === detailRequestNumber}\" id=\"req{{page.requestNumber}}\">\n" +
    "				<td><button ng-click=\"open(page)\" ng-if=\"clickableURLs\" title=\"Open this URL\"><span class=\"glyphicons share green\"></span></button> <span ng-click=\"requestDetails(page)\">{{page.url | limitTo : 70}}</span><span ng-show=\"page.url.length > 70\">...</span></td>\n" +
    "				<td nowrap><span ng-show=\"displayRelativeTimes\">{{page.completedAgoMs | millSecondsToTimeString}} ago</span><span ng-show=\"!displayRelativeTimes\">{{page.completed | date:'short'}}</span></td>\n" +
    "				<td nowrap>\n" +
    "					<span ng-show=\"dosEnabled\" class=\"glyphicons ban\" ng-click=\"addBlock(page.ip)\" title=\"Block {{page.ip}}\"></span>\n" +
    "					<span ng-show=\"clickableIPURL\" class=\"glyphicons globe\" ng-click=\"open(clickableIPURL + page.ip)\" title=\"IP info\"></span>\n" +
    "					{{page.ip}}\n" +
    "				</td>\n" +
    "				<td>{{page.responseCode}}<span ng-show=\"page.timeToFirstByte > 0\">@{{page.timeToFirstByte}}ms</span></td>\n" +
    "				<td>{{page.time}}ms</td>\n" +
    "				<td>{{page.queryCount}}</td>\n" +
    "				<td>{{page.queryTime}}ms</td>\n" +
    "				<td>{{page.longQueryElapsed}}ms</td>\n" +
    "				<td>{{page.longQueryRows}}</td>\n" +
    "				<td><span ng-click=\"requestDetails(page)\">{{page.longQuerySql | limitTo : 70}}</span><span ng-show=\"page.longQuerySql.length > 70\">...</span></td>\n" +
    "			</tr>\n" +
    "		</tbody>\n" +
    "	</table>\n" +
    "</div>");
}]);

angular.module("sidebar.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("sidebar.tpl.html",
    "<ul ng-show=\"showNav\"  class=\"nav nav-pills nav-stacked\" ng-if=\"isAuthenticated\">\n" +
    "	<!-- ng-if=\"isAuthenticated\" -->\n" +
    "	<!-- Server -->\n" +
    "	<li ui-sref=\"server.active\" title=\"Server\"><span class=\"glyphicons imac\" ng-class=\"$state.includes('server') ? 'icon_link' : 'white'\"></span></li>\n" +
    "	<!-- Dashboard -->\n" +
    "	<li ui-sref=\"dashboard.all\" title=\"Dashboard\"><span class=\"glyphicons dashboard\" ng-class=\"$state.includes('dashboard') ? 'icon_link' : 'white'\"></span></li>\n" +
    "	<!-- Counters -->\n" +
    "	<li ui-sref=\"counters\" title=\"Counters\"><span class=\"glyphicons list\" ng-class=\"$state.includes('counters') ? 'icon_link' : 'white'\"></span></li>\n" +
    "	<!-- Configuration -->\n" +
    "	<li ui-sref=\"config\" title=\"Configuration\"><span class=\"glyphicons settings\" ng-class=\"$state.includes('config') ? 'icon_link' : 'white'\"></span></li>\n" +
    "	<!-- Active Monitoring -->\n" +
    "	<li ui-sref=\"monitoring.rules\" title=\"Active Monitoring\"><span class=\"glyphicons cardio\" ng-class=\"$state.includes('monitoring') ? 'icon_link' : 'white'\"></span></li>\n" +
    "	<!-- Stack -->\n" +
    "	<li ui-sref=\"stack.seestack({threadname:'',requestid:''})\" title=\"Stack\"><span class=\"glyphicons show_lines\" ng-class=\"$state.includes('stack') ? 'icon_link' : 'white'\"></span></li>\n" +
    "	<!-- Profiling -->\n" +
    "	<li ui-sref=\"profiling\" title=\"Profiling\"><span class=\"glyphicons stats\" ng-class=\"$state.includes('profiling') || $state.includes('profile') ? 'icon_link' : 'white'\"></span></li>\n" +
    "	<!-- DOS Protection -->\n" +
    "	<li ui-sref=\"dos\" title=\"DOS Protection\"><span class=\"glyphicons shield\" ng-class=\"$state.includes('dos') ? 'icon_link' : 'white'\"></span></li>\n" +
    "	<!-- Logging -->\n" +
    "	<li ui-sref=\"log\" title=\"Log\"><span class=\"glyphicons log_book\" ng-class=\"$state.includes('log') ? 'icon_link' : 'white'\"></span></li>\n" +
    "	<!-- About -->\n" +
    "	<li ui-sref=\"about\" title=\"About\"><span class=\"glyphicons circle_question_mark\"  ng-class=\"$state.includes('about') ? 'icon_link' : 'white'\"></span></li>\n" +
    "	\n" +
    "	<li ng-click=\"logout()\" title=\"Logout\"><span class=\"glyphicons white log_out\"></span></li>\n" +
    "	<!-- <li ng-click=\"toggleNav()\" class=\"dark\"><span class=\"glyphicons white resize_small\"></span></li> -->\n" +
    "</ul> ");
}]);

angular.module("snapshot/seestack.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("snapshot/seestack.tpl.html",
    "<div class=\"seethread\" ng-repeat=\"thread in seestack.threads\">\n" +
    "	<div>\n" +
    "		<abbr ng-show=\"thread.ref\" class=\"threadName {{thread.importance}}\" title=\"{{seestack.infos[thread.ref].description}}\">Thread \"{{thread.name}}\"</abbr>\n" +
    "		<span ng-hide=\"thread.ref\" class=\"threadName {{thread.importance}}\">Thread \"{{thread.name}}\"</span>\n" +
    "	</div>\n" +
    "	<div ng-repeat=\"method in thread.methods\" class=\"method\">\n" +
    "		<span ng-show=\"method.package\">\n" +
    "			<abbr class=\"{{seestack.infos[method.packageInfo].importance}}\" title=\"{{seestack.infos[method.packageInfo].description}}\">{{method.package}}</abbr>\n" +
    "			<abbr ng-show=\"method.methodInfo\" class=\"{{method.importance}}\" title=\"{{seestack.infos[method.methodInfo].description}}\">{{method.rawLineAfterPackage}}</abbr>\n" +
    "			<span ng-hide=\"method.methodInfo\" class=\"{{method.importance}}\">{{method.rawLineAfterPackage}}</span>\n" +
    "		</span>\n" +
    "		<span ng-hide=\"method.package\" class=\"{{method.importance}}\">\n" +
    "			<abbr ng-show=\"method.methodInfo\" class=\"{{method.importance}}\" title=\"{{seestack.infos[method.methodInfo].description}}\">{{method.rawLine}}</abbr>\n" +
    "			<span ng-hide=\"method.methodInfo\" class=\"{{method.importance}}\">{{method.rawLine}}</span>\n" +
    "		</span>\n" +
    "	</div>\n" +
    "</div>\n" +
    "");
}]);

angular.module("snapshot/snapshot.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("snapshot/snapshot.tpl.html",
    "<div class=\"panel\">\n" +
    "	<h3>Snapshot: {{snapshot.timestamp | date: 'medium'}}</h3>\n" +
    "	\n" +
    "	\n" +
    "	<table class=\"table table-striped table-hover table-condensed\">\n" +
    "		<thead>\n" +
    "			<tr>\n" +
    "				<th>Request</th>\n" +
    "				<th>Thread</th>\n" +
    "				<th>Completed</th>\n" +
    "				<th>From</th>\n" +
    "				<th>Response</th>\n" +
    "				<th>Elapsed</th>\n" +
    "				<th>Queries</th>\n" +
    "				<th>QryTime</th>\n" +
    "				<th>LongQry</th>\n" +
    "				<th>Rows</th>\n" +
    "				<th>SQL</th>\n" +
    "			</tr>\n" +
    "		</thead>\n" +
    "		<tbody>\n" +
    "			<tr ng-show=\"snapshot.requests.length==0\">\n" +
    "				<td colspan=\"10\">No active requests.</td>\n" +
    "			</tr>\n" +
    "			<tr ng-repeat=\"page in snapshot.requests\">\n" +
    "				<td>{{page.url}}</td>\n" +
    "				<td>{{page.threadName}}</td>\n" +
    "				<td>{{page.completedFormatted}}</td>\n" +
    "				<td>{{page.ip}}</td>\n" +
    "				<td>{{page.responseCode}}<span ng-show=\"page.timeToFirstByte > 0\">@{{page.timeToFirstByte}}ms</span></td>\n" +
    "				<td>{{page.time}}ms</td>\n" +
    "				<td>{{page.queryCount}}</td>\n" +
    "				<td>{{page.queryTime}}</td>\n" +
    "				<td>{{page.longQueryElapsed}}ms</td>\n" +
    "				<td>{{page.longQueryRows}}</td>\n" +
    "				<td>{{page.longQuerySql}}</td>\n" +
    "			</tr>\n" +
    "		</tbody>\n" +
    "	</table>\n" +
    "	\n" +
    "	\n" +
    "    <ul class=\"nav nav-tabs nav-inner push-down\">\n" +
    "	    <li ng-class=\"{active:$state.includes('snapshot.seestack')}\"><a ui-sref=\"snapshot.seestack\">SeeStack</a></li>\n" +
    "	    <li ng-class=\"{active:$state.includes('snapshot.trace')}\"><a ui-sref=\"snapshot.trace\">Raw Trace</a></li>\n" +
    "    </ul>\n" +
    "\n" +
    "    <div class=\"push-down\" ui-view></div>\n" +
    "	\n" +
    "</div>");
}]);

angular.module("snapshot/trace.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("snapshot/trace.tpl.html",
    "<pre>{{snapshot.stacktrace}}</pre>");
}]);

angular.module("stack/seestack.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("stack/seestack.tpl.html",
    "<div class=\"seethread\" ng-show=\"seestack.threads.length == 0\">No threads found to display.</div>\n" +
    "<div class=\"seethread\" ng-repeat=\"thread in seestack.threads\">\n" +
    "	<div>\n" +
    "		<abbr ng-show=\"thread.ref\" class=\"threadName {{thread.importance}}\" title=\"{{seestack.infos[thread.ref].description}}\">{{thread.name}}</abbr> <span\n" +
    "			ng-hide=\"thread.ref\" class=\"threadName {{thread.importance}}\">{{thread.name}}</span>\n" +
    "	</div>\n" +
    "	<div ng-repeat=\"method in thread.methods\" class=\"method\">\n" +
    "		<span ng-show=\"method.package\"> <abbr class=\"{{seestack.infos[method.packageInfo].importance}}\"\n" +
    "			title=\"{{seestack.infos[method.packageInfo].description}}\">{{method.package}}</abbr> <abbr ng-show=\"method.methodInfo\" class=\"{{method.importance}}\"\n" +
    "			title=\"{{seestack.infos[method.methodInfo].description}}\">{{method.rawLineAfterPackage}}</abbr> <span ng-hide=\"method.methodInfo\"\n" +
    "			class=\"{{method.importance}}\">{{method.rawLineAfterPackage}}</span>\n" +
    "		</span>\n" +
    "		<span ng-hide=\"method.package\" class=\"{{method.importance}}\">\n" +
    "			<abbr ng-show=\"method.methodInfo\" class=\"{{method.importance}}\" title=\"{{seestack.infos[method.methodInfo].description}}\">{{method.rawLine}}</abbr>\n" +
    "			<span ng-hide=\"method.methodInfo\" class=\"{{method.importance}}\">{{method.rawLine}}</span>\n" +
    "		</span>\n" +
    "	</div>\n" +
    "</div>");
}]);

angular.module("stack/stack.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("stack/stack.tpl.html",
    "<div class=\"panel\">\n" +
    "	<div class=\"content\">\n" +
    "		<form>\n" +
    "			<label for=\"filterText\">Thread name filter: <input type=\"text\" name=\"trace-filter\" class=\"trace-filter\" ng-model=\"trace.threadname\" id=\"filterText\"/>\n" +
    "				<button class=\"btn btn-primary btn-sm\" ng-click=\"getStack()\">Trace</button>\n" +
    "				<button class=\"btn btn-default btn-sm\" ng-click=\"doClear()\">Clear</button>\n" +
    "			</label>\n" +
    "			<label for=\"activeOnlyBox\"><input type=\"checkbox\" ng-model=\"trace.activeOnly\" id=\"activeOnlyBox\">Show Active Threads Only</label>\n" +
    "		</form>\n" +
    "	</div>\n" +
    "	\n" +
    "	<div ng-hide=\"rawstack == '' || busy || stackMessage\">\n" +
    "	    \n" +
    "	    <ul class=\"nav nav-tabs nav-inner\">\n" +
    "		    <li ng-class=\"{active:$state.includes('stack.seestack')}\"><a ui-sref=\"stack.seestack\">SeeStack</a></li>\n" +
    "		    <li ng-class=\"{active:$state.includes('stack.trace')}\"><a ui-sref=\"stack.trace\">Raw Trace</a></li>\n" +
    "	    </ul>\n" +
    "\n" +
    "	    <div class=\"push-down\" ui-view></div>\n" +
    "\n" +
    "	</div>\n" +
    "\n" +
    "	<div ng-show=\"stackMessage\" class=\"problemTitle\">\n" +
    "		 <span class=\"glyphicons circle_exclamation_mark\"></span> {{stackMessage}}\n" +
    "	</div>\n" +
    "</div>");
}]);

angular.module("stack/trace.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("stack/trace.tpl.html",
    "<div class=\"seethread\" ng-show=\"seestack.threads.length == 0\">No threads found to display.</div>\n" +
    "<pre>{{rawstack}}</pre>");
}]);

angular.module("templates.common", ["directives/sfMetricWidget.tpl.html"]);

angular.module("directives/sfMetricWidget.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("directives/sfMetricWidget.tpl.html",
    "<div class=\"sf-metric-widget panel panel-default\">\n" +
    "	<div class=\"panel-heading\">\n" +
    "		<span  class=\"pull-right\" ng-if=\"showcontrols\">\n" +
    "			<span class=\"dropdown\" dropdown>\n" +
    "				<span class=\"dropdown-toggle linkish leftMargin\" dropdown-toggle title=\"Refresh Interval\"><span class=\"glyphicons history\"></span>\n" +
    "				<!-- <span ng-hide=\"refreshtime < 1\" class=\"badge\">{{refreshtime}}</span> -->\n" +
    "				<span class=\"caret\"></span></span>\n" +
    "				<ul class=\"dropdown-menu\" dropdown-menu>\n" +
    "				<li>\n" +
    "				<a ng-click=\"setRefresh(0);\" ng-class=\"(refreshtime==0) ? 'highlighted' : ''\">Manually</a>\n" +
    "				</li>\n" +
    "				<li ng-repeat=\"option in refreshOptions\">\n" +
    "				<a ng-click=\"setRefresh(option);\" ng-class=\"(option==refreshtime) ? 'highlighted' : ''\">Every {{option}} Seconds</a>\n" +
    "				</li>\n" +
    "				</ul>\n" +
    "			</span>\n" +
    "			<span class=\"glyphicons refresh leftMargin\" ng-click=\"refresh();\" title=\"Manual Refresh\"></span>	\n" +
    "			<span class=\"glyphicons bin leftMargin\" ng-click=\"gc();\" title=\"Perform Garbage Collection\"></span>	\n" +
    "		</span>\n" +
    "		<h4 class=\"panel-title\">{{title}}</h4> \n" +
    "	</div>\n" +
    "	<!-- <div class=\"panel-body\">\n" +
    "		<div class=\"abstract\" ng-hide=\"details.show\" ng-repeat=\"data in metrics.abstract\">\n" +
    "			<span class=\"badge alert-info pull-right\">{{data.value}}</span> {{data.label}}\n" +
    "		</div>\n" +
    "		<div ng-show=\"details.show\" ng-repeat=\"detail in metrics.details\">\n" +
    "			<div class=\"row\">\n" +
    "				<div class=\"col-sm-4\">{{detail.counter}}</div>\n" +
    "				<div class=\"col-sm-8 text-right\"><span class=\"badge alert-info\">{{detail.pageCount}}</span> @ {{detail.time}}ms</div>\n" +
    "			</div>\n" +
    "		</div> \n" +
    "	</div> -->\n" +
    "	<div class=\"panel-body\">\n" +
    "			<div class=\"row\">\n" +
    "				<div class=\"col-xs-6\">\n" +
    "					<table>\n" +
    "						<tr ng-repeat=\"data in metrics.abstract\">\n" +
    "							<td>{{data.label}} </td> \n" +
    "							<td class=\"text-right\"> \n" +
    "								<span class=\"badge alert-info\" ng-hide=\"data.label === 'Updated'\">{{data.value | formatIfNumber}}</span>\n" +
    "								<span class=\"badge alert-info\" ng-show=\"data.label === 'Updated'\">\n" +
    "									<span ng-hide=\"data.sameDay\">{{data.value | date:'d-MMM'}}</span>\n" +
    "									<span>{{data.value | date:'h:mm a'}}</span>\n" +
    "								</span>\n" +
    "							</td>\n" +
    "						</tr>\n" +
    "					</table>\n" +
    "				</div>\n" +
    "				<div class=\"col-xs-6\" ng-if=\"metrics.details.length\">\n" +
    "					<table>\n" +
    "						<tr ng-repeat=\"detail in metrics.details\">\n" +
    "							<td class=\"text-right\">{{detail.counter}}</td> \n" +
    "							<td> <span class=\"label label-{{detail.color}} label-as-badge leftMargin\">{{detail.pageCount | formatIfNumber}}</span></td>\n" +
    "							<td> @ {{detail.time | formatIfNumber}}ms</td>\n" +
    "						</tr>\n" +
    "					</table>\n" +
    "				</div> \n" +
    "				<div class=\"col-xs-6\" ng-if=\"metrics.info.length\">\n" +
    "					<table>\n" +
    "						<tr ng-repeat=\"detail in metrics.info\">\n" +
    "							<td class=\"text-right\"><span class=\"text-capitalize\">{{detail.item}}</span></td> \n" +
    "							<td><span class=\"leftMargin\">{{detail.value | formatIfNumber}}</span></td>\n" +
    "						</tr>\n" +
    "					</table>\n" +
    "				</div> \n" +
    "			</div> \n" +
    "	</div> \n" +
    "</div> \n" +
    "");
}]);
