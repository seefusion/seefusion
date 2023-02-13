angular.module('resources.servers',[])

.factory('ServerService', ['$http', '$log', function ($http, $log) {

	var Servers = {};

	Servers.getServers = function(mode){
		if (mode.toUpperCase() !== "INCIDENTS") {
			var request = $http.get("/json/getdashboardservers?mode=" + mode);
			return request.then(function(response){
				var servers = response.data.servers;
				return servers;
			});
		}
		else {
			var servers = {};
			return servers;
		}
	};

	Servers.garbageCollection = function(){
		var request = $http.get("/json/gc");
		return request.then(function(response){
			return response.data;
		});
	};

	Servers.getSeeFusionInfo = function(){
		var request = $http.get("/json/serverinfo");
		return request.then(function(response){
			return response.data.seefusion;
		});
	};

	Servers.getServerInfo = function(){
		var request = $http.get("/json/serverinfo");
		return request.then(function(response){
			return response.data.server;
		});
	};

	Servers.getInfo = function(){
		var request = $http.get("/json/serverinfo");
		return request.then(function(response){
			var data = response.data;
			var metrics = {};
			var color = "info";
			var avgTime = 0;
			var i = 0;
			var counters = "";
			var tempMem = 0;
			var memString = '';

			// BEGIN - Server widget data
				var server = {};
				server['abstract'] = [];
				server['details'] = [];
				server['info'] = [];

				var memory = Math.round((data.server.memory.used/data.server.memory.total) * 100);
				var lastUpdated = new Date(data.timestamp);

				var d = new Date();
				var sameDay = (d.toDateString() === lastUpdated.toDateString());

				memory = memory.toString() + '%';
				server['abstract'].push({label:'Memory',value:memory});
				server['abstract'].push({label:'Uptime',value:data.server.uptime});
				server['abstract'].push({label:'Updated',value:lastUpdated,sameDay:sameDay});

				// add total/used/free memory in info, displays as name/value in the widget
				counters = ["total","used","free"];
				for (i = 0; i < counters.length; i++) {
					tempMem = Math.ceil(data.server.memory[counters[i]] / 1000000);
					if(tempMem > 1000) {
						memString = (tempMem / 1000).toFixed(2).toString() + " GB";
					}
					else {
						memString = tempMem.toString() + " MB"; 
					}
					server['info'].push(
						{
							item:counters[i],
							value:memString, 
							color:"info"
						}
					);
				}

				metrics['server'] = server;
				metrics['serverName'] = data.server.name;
			// END - Server widget data


			// BEGIN - Requests widget data
				var requests = {};
				requests['abstract'] = [];
				requests['details'] = [];	

				requests['abstract'].push({label:'Active',value:data.server.currentRequests });
				requests['abstract'].push({label:'Total',value:data.seefusion.totalPages });

				metrics['requests'] = requests;
			// END - Requests widget data

			

			// BEGIN - Queries widget data
				var queries = {};
				queries['abstract'] = [];
				queries['details'] = [];

				queries['abstract'].push({label:'Active',value:data.server.currentQueries });
				queries['abstract'].push({label:'Total',value:data.seefusion.totalQueries });

				metrics['queries'] = queries;	
			// END - Queries widget data



			// BEGIN - Populate details arrays with counters info
				// We have counters at 1s, 10s, and 60s
				counters = [1,10,60];

				for (i = 0; i < counters.length; i++) {
					//set the color of the text based on the threshold
					color="success"; //green, default
					avgTime = data['counters' + counters[i]].avgPageTimeMs;
					if (avgTime >= data.seefusion.slowPageThreshold) {color = "danger";} //red
					else if (avgTime >= (data.seefusion.slowPageThreshold * 0.8)) {color = "warning";} //yellow

					requests['details'].push(
						{
							counter:counters[i] + 's',
							pageCount:data['counters' + counters[i]].pageCount,
							time:data['counters' + counters[i]].avgPageTimeMs,
							color:color
						}
					);
					//set the color of the text based on the threshold
					color="success"; //green, default
					avgTime = data['counters' + counters[i]].avgQueryTimeMs;
					if (avgTime >= data.seefusion.slowQueryThreshold) {color = "danger";} //red
					else if (avgTime >= (data.seefusion.slowQueryThreshold * 0.8)) {color = "warning";} //yellow

					queries['details'].push(
						{
							counter:counters[i] + 's',
							pageCount:data['counters' + counters[i]].queryCount,
							time:data['counters' + counters[i]].avgQueryTimeMs,
							color:color
						}
					);
				}
 
			// END - Populate details arrays with counters info

			return metrics;
		});
	};

	return Servers;

}]);