angular.module('services.AppAlert', [])

.factory('AppAlert', ['$rootScope', '$timeout', function($rootScope, $timeout) {
		
		var alertService;
		$rootScope.alerts = [];
		
		return alertService = {
			add: function(type, msg, timeout) {
				$rootScope.alerts.push({
					type: type,
					msg: msg,
					close: function() {
						return this.closeAlert(this);
					}
				});

				if(timeout){
					$timeout(function(){
						alertService.closeAlert(this);
					}, timeout);
				}
			},
			closeAlert: function(alert) {
				return this.closeAlertIdx($rootScope.alerts.indexOf(alert));
			},
			closeAlertIdx: function(index) {
				return $rootScope.alerts.splice(index, 1);
			},
			clear: function(){
				$rootScope.alerts = [];
			}
		};
	}
]);