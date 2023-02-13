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

	return Config;

}]);