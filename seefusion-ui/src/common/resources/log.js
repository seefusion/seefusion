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