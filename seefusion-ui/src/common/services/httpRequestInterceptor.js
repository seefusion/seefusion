angular.module('services.httpRequestInterceptor',[])
.factory('httpRequestInterceptor', ['$rootScope', function($rootScope) {

	return {
		request: function ($config){
			$config.headers = {'X-SeeFusion-Auth':$rootScope.authToken};
			return $config;
		}
	};
}]);