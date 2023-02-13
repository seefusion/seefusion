angular.module('services.httpResponseEvaluation',[])
.factory('httpResponseEvaluation', ['$rootScope', '$q', '$log', function($rootScope, $q, $log) {
	var responseStatus = {
		response: function(response){
			if(response.headers()['content-type'] === "application/json;charset=UTF-8"){

				var data = response.data;
				var errors = response.data.errors;

				// If data doesn't exist reject.				
				if(!data){
					return $q.reject(response);
				}

				// If the API returns success = false, reject.
				if(!data.success){
					return $q.reject(errors);	
				}
			}
			return response;
		},
		responseError: function (response) {
			$log.warn(response);
			//$rootScope.problem={isProblem:true,status:response.status};
			$rootScope.$emit('httpResponseAlert',response);
			return $q.reject(response);
		}
	};

	return responseStatus;
}]);



