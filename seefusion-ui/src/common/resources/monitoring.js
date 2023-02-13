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