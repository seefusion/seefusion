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