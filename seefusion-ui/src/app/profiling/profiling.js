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
