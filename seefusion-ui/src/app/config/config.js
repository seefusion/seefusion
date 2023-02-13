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

	// Save the config changes and refresh the model.
	$scope.saveConfigOld = function(item){
		ConfigService.save(item).then(function(response){
			$scope.config = response;
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
