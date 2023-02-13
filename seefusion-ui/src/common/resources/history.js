angular.module('resources.history',[])

.factory('HistoryService', ['$http', '$log', function ($http, $log) {

	var History = {};


	History.getHistory = function(){
		var request = $http.get("/json/gethistoryminutes");
		return request.then(function(response){

			var i;
			var history = response.data.history;
			for (i = 0; i < history.length; ++i) {
				
					history[i].x = new Date(history[i].timestamp);
				
			}
			return history;
		});
		
	};

	History.getSnapshot = function(timestamp){
		var request = $http.get("/json/gethistorysnapshot?timestamp=" + timestamp);
		return request.then(function(response){
			var results = {};
			results['seestack'] = response.data.seestack;
			results['snapshot'] = response.data.snapshot;
			return results;
		});
		
	};

	History.getStack = function(threadname,requestid,isActive){
		var request = $http.get("/json/getstack?threadname=" + threadname + "&requestid=" + requestid + "&activeonly=" + isActive);
		return request.then(function(response){
			var results = {};
			results['seestack'] = response.data.seestack;
			results['rawstack'] = response.data.rawstack;
			results['message'] = response.data.message;
			return results;
		});
		
	};

	// Get the pageCount and queryCount
	History.getPages = function(data){
		var history = data; 
		var keys = [];
		var pageCounts = [];
		var queryCounts = [];
			
		history.forEach(function(item){
			var pageCount = [];
			pageCount.push(Number(item.timestamp));
			pageCount.push(Number(item.pageCount));
			pageCounts.push(pageCount);

			var queryCount = [];
			queryCount.push(Number(item.timestamp));
			queryCount.push(Number(item.queryCount));
			queryCounts.push(queryCount);
		});

		var pages = {};
		pages['key'] = 'Pages';
		pages['values'] = pageCounts;

		var queries = {};
		queries['key'] = 'Queries';
		queries['color'] = 'orange';
		queries['values'] = queryCounts;

		keys.push(pages);
		keys.push(queries);

		return keys;
	};

	// Get the pageTime and queryTime
	History.getQueries = function(data){
		var history = data;
		var keys = [];
		var pageTimes = [];
		var queryTimes = [];
		
		history.forEach(function(item){
			var pageTime = [];
			pageTime.push(Number(item.timestamp));
			pageTime.push(Number(item.avgPageTimeMs));
			pageTimes.push(pageTime);

			var queryTime = [];
			queryTime.push(Number(item.timestamp));
			queryTime.push(Number(item.avgQueryTimeMs));
			queryTimes.push(queryTime);
		});

		var pages = {};
		pages['key'] = 'Avg Page Time';
		pages['values'] = pageTimes;

		var queries = {};
		queries['key'] = 'Avg Query Time';
		queries['color'] = 'orange';
		queries['values'] = queryTimes;

		keys.push(pages);
		keys.push(queries);

		return keys;
	};

	// Get the memory details
	History.getMemory = function(data){
		var history = data;
		var keys = [];
		var memoryTotals = [];
		var memoryUsedTotals = [];
		
		history.forEach(function(item){
			var memoryTotal = [];
			memoryTotal.push(Number(item.timestamp));
			memoryTotal.push(Number(item.memoryTotalMiB));
			memoryTotals.push(memoryTotal);

			var memoryUsedTotal = [];
			memoryUsedTotal.push(Number(item.timestamp));
			memoryUsedTotal.push(Number(item.memoryUsedMiB));
			memoryUsedTotals.push(memoryUsedTotal);
		});

		var pages = {};
		pages['key'] = 'Available';
		pages['values'] = memoryTotals;

		var queries = {};
		queries['key'] = 'Used';
		queries['color'] = 'orange';
		queries['values'] = memoryUsedTotals;

		keys.push(pages);
		keys.push(queries);

		return keys;
	};

	return History;
}]);