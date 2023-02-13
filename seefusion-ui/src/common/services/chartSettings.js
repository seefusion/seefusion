angular.module('services.ChartSettings', [])

.factory('ChartSettings', ['$timeout', function($timeout) {
		
		var service = {

			xAxisFormatDate: function(){
				return function(d){
					return d3.time.format('%H:%M')(new Date(d));
				};
			}		
		};

		return service;

	}
]);