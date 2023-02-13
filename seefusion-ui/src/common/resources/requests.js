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