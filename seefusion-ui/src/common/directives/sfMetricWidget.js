angular.module('directives.sfMetricWidget', [])

// NOTES
// Incoming metrics should be in the following format:
// abstract[
//  {'label':'Uptime','value':'10'},
//  {'label':'Memory','value':'23%'}
// ],
// details[
//  {'counter':'1s','pageCount':5,'time':25},
//  {'counter':'10s','pageCount':5,'time':25},
//  {'counter':'60s','pageCount':5,'time':25}
// ],
// info[
//  {'item':'Total','value':'250mb'},
//  {'item':'Used','value':'30mb'},



.directive('sfMetricWidget', ['$parse', '$log', function($parse, $log) {
	
	return{
		restrict: 'EA',
		scope: {
			metrics: '=',
			title: '=',
			refreshtime: '=',
			showcontrols: '=',
			gc: '&',
			refresh: '&',
			setpodrefresh: '&'
		},
		templateUrl: 'directives/sfMetricWidget.tpl.html',
		link: function (scope, element, attrs) {

			scope.$watch('metrics',function(metrics){
				//
			});

			scope.details = {show:false};
			scope.showDetails = function(metrics){
				if(metrics.details.length){
					scope.details['show'] = true;
				}
			};

			scope.hideDetails = function(){
				scope.details['show'] = false;
			};

			scope.setRefresh = function(sec) {
				scope.setpodrefresh({ sec:sec });
			};

			scope.refreshOptions = [5,10,30,60];
		}
	};

}]);
