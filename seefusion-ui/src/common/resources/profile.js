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