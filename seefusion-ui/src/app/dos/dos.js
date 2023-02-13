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