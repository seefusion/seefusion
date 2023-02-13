angular.module('login', [])

.controller('LoginCtrl', ['$scope', '$modalInstance', 'UserService', '$timeout', '$rootScope', function ($scope, $modalInstance, UserService, $timeout, $rootScope) {

	$timeout(function () {
	        $('#loginpassword').focus();
	      }, 100);

	$scope.password = null;

	$scope.login = function(){
		if($scope.password){
			UserService.login($scope.password).then(function(isAuthenticated){
				if (isAuthenticated) {
					$modalInstance.close();
					$rootScope.$emit('globalRefreshTrigger');
				}
			});
		}
	};

}]);
