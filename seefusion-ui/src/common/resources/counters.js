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