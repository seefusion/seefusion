angular.module('security.authorization', ['security.user'])

// This service provides guard methods to support AngularJS routes.
// You can add them as resolves to routes to require authorization levels
// before allowing a route change to complete
.provider('securityAuthorization', {

	isAuthenticated: ['securityAuthorization', function(securityAuthorization){
		return securityAuthorization.isAuthenticated();
	}],

	canConfig: ['securityAuthorization', function(securityAuthorization){
		return securityAuthorization.canConfig();
	}],	

	canKill: ['securityAuthorization', function(securityAuthorization){
		return securityAuthorization.canKill();
	}],

	$get: ['UserService', function(UserService) {
		var service = {

			isAuthenticated: function(){
				return UserService.isAuthenticated();
			},

			canConfig: function(){
				return UserService.canConfig();
			},

			canKill: function(){
				return UserService.canKill();
			}


	};

	return service;
	}]
});