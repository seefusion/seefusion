// Based loosely around work by Witold Szczerba - https://github.com/witoldsz/angular-http-auth

// Add this to all http requests.
// X-SeeFusion-Auth

angular.module('security.user', [])

.factory('UserService', ['$http', '$rootScope', '$q', '$log', '$location', '$state', '$window', function($http, $rootScope, $q, $log, $location, $state, $window) {

	// Redirect to the given url (defaults to '/')
	function redirect(url) {
		url = url || '/';
		$location.path(url);
	}

	function storeSession(token){
		$window.sessionStorage.token = token;
	}

	function deleteSession(){
		delete $window.sessionStorage.token;
	}

	var credentials = {
		canAuthenticate: false,
		canConfig: false,
		canKill: false
	};

	var setCredentials = function(auth,config,kill){
		credentials.canAuthenticate = auth;
		credentials.canConfig = config;
		credentials.canKill = kill;
	};

	var authToken = null;

	var authenticated = true;


	// The public API of the service
	var service = {

		login: function(password){
			var request;

			if(password){
				request = $http.post("json/auth", {password:password});
			} else {
				request = $http.post("json/auth", {password:null});
			}

			return request.then(function(response){

				setCredentials(
					response.data.seefusion.canRead,
					response.data.seefusion.canConfig,
					response.data.seefusion.canKill
				);

				if(response.data.auth){
					authToken = response.data.auth;
					storeSession(authToken);
					isAuthenticated = true;
				} else {
					$log.warn('Could not authenticate.');
					$rootScope.$emit('showLogin');
					isAuthenticated = false;
				}
				return isAuthenticated;
			});

		},

		logout: function(){
			credentials = null;
			var request = $http.get("/json/logout");
			return request.then(function(response){
				deleteSession();
				$log.info(response);
			});
		},

		isAuthenticated: function(){
			return authenticated;
		},

		getCredentials: function(){
			return credentials;
		},

		canConfig: function(){
			return credentials.canConfig;
		},

		canKill: function(){
			return credentials.canKill;
		},

		getToken: function(){
			return authToken;
		},

		setToken: function(token){
			authToken = token;
		},

		getSessionToken: function(){
			return $window.sessionStorage.token;
		}

	};

	return service;
}]);
