angular.module("templates.app", ["about/about.tpl.html", "config/config.tpl.html", "counters/counters.tpl.html", "dashboard/dashboard.tpl.html", "dashboard/incident.tpl.html", "dashboard/incidents.tpl.html", "dashboard/servers.tpl.html", "dos/dos.tpl.html", "dos/list.tpl.html", "dos/settings.tpl.html", "header.tpl.html", "help/help.tpl.html", "info/connection-modal.tpl.html", "info/connectivity.tpl.html", "info/info.tpl.html", "info/license.tpl.html", "log/log.tpl.html", "login/login-modal.tpl.html", "monitoring/edit.tpl.html", "monitoring/incidents.tpl.html", "monitoring/monitoring.tpl.html", "monitoring/rules.tpl.html", "nav.tpl.html", "navExpand.tpl.html", "notifications.tpl.html", "popup.tpl.html", "problemHeader.tpl.html", "profiling/profile.tpl.html", "profiling/profiling.tpl.html", "server/active.tpl.html", "server/chart-modal.tpl.html", "server/chart-modal2.tpl.html", "server/recent.tpl.html", "server/requestDetails.tpl.html", "server/server.tpl.html", "server/slow.tpl.html", "sidebar.tpl.html", "snapshot/seestack.tpl.html", "snapshot/snapshot.tpl.html", "snapshot/trace.tpl.html", "stack/seestack.tpl.html", "stack/stack.tpl.html", "stack/trace.tpl.html"]);

angular.module("about/about.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("about/about.tpl.html",
    "<h1>About</h1>\n" +
    "\n" +
    "<img src=\"images/loading.gif\" ng-show=\"busy\">\n" +
    "<p ng-hide=\"busy\">\n" +
    "Version {{about.version}} build {{about.buildNumber}}\n" +
    "</p>\n" +
    "\n" +
    "<p class=\"sf-copyright push-down\">\n" +
    "	<div>SeeFusion copyright &copy; 2005-{{about.year}} Daryl Banttari Consulting</div>\n" +
    "	<div><a href=\"http://angularjs.org\" target=\"_blank\">AngularJS</a> copyright &copy; 2013 Google, used under the MIT license.</div>\n" +
    "	<div><a href=\"http://jquery.org\" target=\"_blank\">JQuery</a> copyright &copy; 2013 The JQuery Foundation, used under the MIT license.</div>\n" +
    "	<div><a href=\"http://angular-ui.github.io/bootstrap/\" target=\"_blank\">AngularUI</a> copyright &copy; 2012 - 2014 the AngularUI Team, used under the MIT license.</div>\n" +
    "	<div><a href=\"http://d3js.org\" target=\"_blank\">D3</a> copyright &copy; 2012 Michael Bostock, used under the BSD license.</div>\n" +
    "	<div><a href=\"https://github.com/cmaurer/angularjs-nvd3-directives\" target=\"_blank\">angularjs-nvd3-directives</a> copyright &copy; 2013 n3-charts, used under the Apache license.</div>\n" +
    "	<div><a href=\"http://getbootstrap.com\" target=\"_blank\">Twitter Bootstrap</a> copyright &copy; 2013 Twitter, used under the MIT license.</div>\n" +
    "</p>");
}]);

angular.module("config/config.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("config/config.tpl.html",
    "<!-- <div>\n" +
    "	<h3>You must be logged in with Config authorization to use this page.</h3>\n" +
    "</div> -->\n" +
    "<div class=\"content\">\n" +
    "	<tabset class=\"config\">\n" +
    "		<tab ng-repeat=\"section in config.sections\" heading=\"{{section.title}}\" data-spy=\"scroll\" data-target=\"#confignav\" active=\"section.isActive\" select=\"tabSelected(section.title)\">\n" +
    "			<a name=\"{{section.title}}\"></a>\n" +
    "\n" +
    "			<div class=\"table-responsive\" ng-hide=\"section.title === 'Database Logging'\">\n" +
    "			<table class=\"table table-striped table-hover\">\n" +
    "			<tbody>\n" +
    "				<tr ng-repeat=\"item in section.items\">\n" +
    "					<td class=\"col-sm-2\"><strong>{{item.configitem}}</strong></td>\n" +
    "					<td class=\"col-sm-7\" ng-switch=\"item.type\">\n" +
    "						<span ng-switch-when=\"booleantrue\" class=\"btn-group\">\n" +
    "							<toggle-switch class=\"switch-success\" ng-model=\"item.value\" on-label=\"Enabled\" off-label=\"Disabled\" ng-value=\"item.value\" on-change=\"saveConfig(item,$parent.$parent.$index)\"></toggle-switch>\n" +
    "						</span>\n" +
    "						<span ng-switch-when=\"booleanfalse\" class=\"btn-group\">\n" +
    "							<toggle-switch class=\"switch-success\" ng-model=\"item.value\" on-label=\"Enabled\" off-label=\"Disabled\" ng-value=\"item.value\" on-change=\"saveConfig(item,$parent.$parent.$index)\"></toggle-switch>\n" +
    "						</span>\n" +
    "						<span ng-switch-when=\"boolean\" class=\"btn-group\">\n" +
    "							<toggle-switch class=\"switch-success\" ng-model=\"item.value\" on-label=\"Enabled\" off-label=\"Disabled\" ng-value=\"item.value\" on-change=\"saveConfig(item,$parent.$parent.$index)\"></toggle-switch>\n" +
    "						</span>\n" +
    "						<span ng-switch-when=\"text\" class=\"input-append\" ng-click=\"item.editing=true\">\n" +
    "							<input name=\"item{{$index}}\" type=\"text\" class=\"input-xxlarge fatty\" ng-model=\"item.value\" id=\"item{{$index}}\" ng-disabled=\"!item.editing\">\n" +
    "							<button class=\"btn btn-sm btn-default\" type=\"button\" ng-hide=\"item.editing\" ng-click=\"item.editing=true\">Edit</button>\n" +
    "							<button class=\"btn btn-sm btn-primary\" type=\"button\" ng-click=\"saveConfig(item,$parent.$parent.$index)\" ng-show=\"item.editing\">Save</button>\n" +
    "						</span>\n" +
    "						<span ng-switch-when=\"password\" class=\"input-append\" ng-click=\"item.editing=true\">\n" +
    "							<input name=\"item{{$index}}\" type=\"password\" class=\"input-xxlarge fatty\" ng-model=\"item.value\" id=\"item{{$index}}\" ng-disabled=\"!item.editing\">\n" +
    "							<button class=\"btn btn-sm btn-default\" type=\"button\" ng-hide=\"item.editing\" ng-click=\"item.editing=true\">Edit</button>\n" +
    "							<button class=\"btn btn-sm btn-primary\" type=\"button\" ng-click=\"saveConfig(item,$parent.$parent.$index)\" ng-show=\"item.editing\">Save</button>\n" +
    "						</span>\n" +
    "						<span ng-switch-when=\"integer\" class=\"input-append\" ng-click=\"item.editing=true\">\n" +
    "							<input name=\"item{{$index}}\" numericbinding type=\"number\" class=\"input-mini\" ng-model=\"item.value\" id=\"item{{$index}}\" min=\"0\" ng-disabled=\"!item.editing\" >\n" +
    "							<button class=\"btn btn-sm btn-default\" type=\"button\" ng-hide=\"item.editing\" ng-click=\"item.editing=true\">Edit</button>\n" +
    "							<button class=\"btn btn-sm btn-primary\" type=\"button\" ng-click=\"saveConfig(item,$parent.$parent.$index)\" ng-show=\"item.editing\">Save</button>\n" +
    "						</span>\n" +
    "						<span ng-switch-when=\"longtext\" class=\"input-append\" ng-click=\"item.editing=true\">\n" +
    "							<textarea name=\"item{{$index}}\" ng-model=\"item.value\" class=\"input-xxlarge fatty\" rows=5 id=\"item{{$index}}\" ng-disabled=\"!item.editing\"></textarea>\n" +
    "							<button class=\"btn btn-sm btn-default\" type=\"button\" ng-hide=\"item.editing\" ng-click=\"item.editing=true\">Edit</button>\n" +
    "							<button class=\"btn btn-sm btn-primary\" type=\"button\" ng-click=\"saveConfig(item,$parent.$parent.$index)\" ng-show=\"item.editing\">Save</button>\n" +
    "						</span>\n" +
    "						<span ng-switch-when=\"license\" class=\"input-append\" ng-click=\"item.editing=true\">\n" +
    "							<textarea name=\"item{{$index}}\" ng-model=\"item.value\" class=\"input-xxlarge fatty\" rows=5 id=\"item{{$index}}\" ng-disabled=\"!item.editing\"></textarea>\n" +
    "							<button class=\"btn btn-sm btn-default\" type=\"button\" ng-hide=\"item.editing\" ng-click=\"item.editing=true\">Edit</button>\n" +
    "							<button class=\"btn btn-sm btn-primary\" type=\"button\" ng-click=\"saveConfig(item,$parent.$parent.$index)\" ng-show=\"item.editing\">Save</button>\n" +
    "						</span>\n" +
    "						<span ng-switch-when=\"select\" class=\"input-append\" ng-click=\"item.editing=true\">\n" +
    "							<select name=\"item{{$index}}\" ng-model=\"item.value\" ng-options=\"value for value in item.options\" id=\"item{{$index}}\" ng-disabled=\"!item.editing\"></select>\n" +
    "							<button class=\"btn btn-sm btn-primary\" type=\"button\" ng-click=\"saveConfig(item,$parent.$parent.$index)\" ng-show=\"item.editing\">Save</button>\n" +
    "						</span>\n" +
    "						<span ng-switch-when=\"createtables\">\n" +
    "							<button type=\"button\" class=\"btn btn-sm btn-primary\" value=\"true\" ng-click=\"createTables()\">&nbsp;Create Tables&nbsp;</button>\n" +
    "						</span>\n" +
    "						<span ng-switch-when=\"dbdriver\" class=\"input-append\" ng-click=\"item.editing=true\">\n" +
    "							<select name=\"item{{$index}}\" ng-model=\"item.value\" ng-options=\"value.driver as value.label for value in dbTypes\" id=\"item{{$index}}\" ng-disabled=\"!item.editing\"></select>\n" +
    "							<button class=\"btn btn-sm btn-default\" type=\"button\" ng-hide=\"item.editing\" ng-click=\"item.editing=true\">Edit</button>\n" +
    "							<button class=\"btn btn-sm btn-primary\" type=\"button\" ng-click=\"saveConfig(item,$parent.$parent.$index)\" ng-show=\"item.editing\">Save</button>\n" +
    "						</span>\n" +
    "\n" +
    "						<span ng-switch-default>\n" +
    "							<pre>{{item|json}}</pre>\n" +
    "						</span>\n" +
    "						<span>\n" +
    "							<img src=\"images/loading.gif\" ng-show=\"item.busy\" />\n" +
    "							<img src=\"images/messages/error.png\" ng-show=\"item.error\" />\n" +
    "							<img src=\"images/messages/success.png\" ng-show=\"item.saved\" />\n" +
    "							<span class=\"error-message\">{{item.errorMessage}}</span>\n" +
    "						</span>\n" +
    "					</td>\n" +
    "					<td class=\"col-sm-3\">{{item.description}}</td>\n" +
    "				</tr>\n" +
    "			</tbody>\n" +
    "			</table>\n" +
    "		</div>	\n" +
    "\n" +
    "		<div ng-show=\"section.title === 'Database Logging'\">\n" +
    "			<!-- display this as a multi field form, rather than each item having its own save button -->\n" +
    "			<form name=\"dbSettingsForm\" novalidate>\n" +
    "			<table class=\"table table-striped table-hover\">\n" +
    "			<tbody>				 \n" +
    "				<tr ng-repeat=\"item in section.items\">\n" +
    "					<td class=\"col-sm-2\"><label for=\"item{{$index}}\"><strong>{{item.configitem}}</strong></label></td>\n" +
    "					<td class=\"col-sm-7\" ng-switch=\"item.type\">\n" +
    "						<span ng-switch-when=\"booleantrue\" class=\"btn-group\">\n" +
    "							<toggle-switch class=\"switch-success\" ng-model=\"item.value\" on-label=\"Enabled\" off-label=\"Disabled\" ng-value=\"item.value\"></toggle-switch>\n" +
    "						</span>\n" +
    "\n" +
    "						<span ng-switch-when=\"boolean\" class=\"btn-group\">\n" +
    "							<toggle-switch class=\"switch-success\" ng-model=\"item.value\" on-label=\"Enabled\" off-label=\"Disabled\" ng-value=\"item.value\"></toggle-switch>\n" +
    "						</span>\n" +
    "						\n" +
    "						<span ng-switch-when=\"text\" class=\"input-append\">\n" +
    "							<input name=\"item{{$index}}\" type=\"text\" class=\"input-xxlarge fatty\" ng-model=\"item.value\" id=\"item{{$index}}\">\n" +
    "						</span>\n" +
    "						<span ng-switch-when=\"password\" class=\"input-append\">\n" +
    "							<input name=\"item{{$index}}\" type=\"password\" class=\"input-xxlarge fatty\" ng-model=\"item.value\" id=\"item{{$index}}\">\n" +
    "						</span>\n" +
    "						<span ng-switch-when=\"integer\" class=\"input-append\">\n" +
    "							<input name=\"item{{$index}}\" numericbinding type=\"number\" class=\"input-mini\" ng-model=\"item.value\" id=\"item{{$index}}\">\n" +
    "						</span>\n" +
    "						<span ng-switch-when=\"longtext\" class=\"input-append\">\n" +
    "							<textarea name=\"item{{$index}}\" ng-model=\"item.value\" class=\"input-xxlarge fatty\" rows=5 id=\"item{{$index}}\"></textarea>\n" +
    "						</span>\n" +
    "						<span ng-switch-when=\"license\" class=\"input-append\">\n" +
    "							<textarea name=\"item{{$index}}\" ng-model=\"item.value\" class=\"input-xxlarge fatty\" rows=5 id=\"item{{$index}}\"></textarea>\n" +
    "						</span>\n" +
    "						<span ng-switch-when=\"select\" class=\"input-append\">\n" +
    "							<select name=\"item{{$index}}\" ng-model=\"item.value\" ng-options=\"value for value in item.options\" id=\"item{{$index}}\"></select>\n" +
    "						</span>\n" +
    "						<span ng-switch-when=\"dbdriver\" class=\"input-append\">\n" +
    "							<select name=\"item{{$index}}\" ng-style=\"{'width': {{dbTypeMaxLength}} + 'em'}\" ng-model=\"item.value\" id=\"item{{$index}}\">\n" +
    "								<option value=\"{{item.value}}\" ng-show=\"!arrayContains(item.value,dbTypeArray)\">{{item.value}}</option>\n" +
    "								<option ng-repeat=\"option in dbTypeArray\" value=\"{{option}}\">{{option}}</option>\n" +
    "							</select>\n" +
    "						</span>\n" +
    "						<span ng-switch-default>\n" +
    "							<pre>{{item|json}}</pre>\n" +
    "						</span>\n" +
    "						<span>\n" +
    "							<img src=\"images/loading.gif\" ng-show=\"item.busy\" />\n" +
    "							<img src=\"images/messages/error.png\" ng-show=\"item.error\" />\n" +
    "							<img src=\"images/messages/success.png\" ng-show=\"item.saved\" />\n" +
    "							<span class=\"error-message\">{{item.errorMessage}}</span>\n" +
    "						</span>\n" +
    "					</td>\n" +
    "					<td class=\"col-sm-3\">{{item.description}}</td>\n" +
    "				</tr>\n" +
    "				\n" +
    "				<tr>\n" +
    "					<td class=\"col-sm-2\"> </td>\n" +
    "					<td class=\"col-sm-7\" ng-switch=\"item.type\">\n" +
    "						<button class=\"btn btn-sm btn-primary\" type=\"button\" ng-click=\"saveConfigs(section,$index)\" ng-show=\"dbSettingsForm.$dirty\">Save Settings</button>\n" +
    "						<button class=\"btn btn-sm btn-success\" type=\"button\" ng-show=\"configsSaved && !dbSettingsForm.$dirty\"><span class=\"glyphicons white check\"></span> Saved</button>\n" +
    "					</td>\n" +
    "					<td class=\"col-sm-3\"> </td>\n" +
    "				</tr>\n" +
    "			</tbody>\n" +
    "			</table>\n" +
    "			</form>\n" +
    "		</div>\n" +
    "		</tab>\n" +
    "\n" +
    "	</tabset>\n" +
    "</div>\n" +
    "\n" +
    "\n" +
    "");
}]);

angular.module("counters/counters.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("counters/counters.tpl.html",
    "<div ng-show=\"initialLoading\" class=\"alert relative alert-info\">\n" +
    "	<img src=\"/img/spinner.gif\"> Loading Counters...\n" +
    "</div>\n" +
    "\n" +
    "<div ng-hide=\"initialLoading\">	\n" +
    "	<!-- <div class=\"row\">\n" +
    "		<div class=\"col-sm-12 text-right\"><label>Search: <input ng-model=\"searchText\"></label></div>\n" +
    "	</div> --> \n" +
    "	<p><button class=\"btn btn-sm btn-primary\" type=\"button\" ng-click=\"clear()\">Clear</button></p>\n" +
    "	\n" +
    "	<table class=\"table table-striped table-hover\">\n" +
    "		<tr>\n" +
    "			<th ng-click=\"sortType = 'timestamp'; sortReverse = !sortReverse\">\n" +
    "	           Timestamp \n" +
    "	            <span ng-show=\"sortType == 'timestamp' && !sortReverse\" class=\"caret\"></span>\n" +
    "	            <span ng-show=\"sortType == 'timestamp' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "			</th>\n" +
    "			<th  align=\"center\" ng-click=\"sortType = 'pageCount'; sortReverse = !sortReverse\">\n" +
    "	           Completed Reqs\n" +
    "	            <span ng-show=\"sortType == 'pageCount' && !sortReverse\" class=\"caret\"></span>\n" +
    "	            <span ng-show=\"sortType == 'pageCount' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span> \n" +
    "			</th>\n" +
    "			<th  align=\"center\" ng-click=\"sortType = 'avgPageTimeMs'; sortReverse = !sortReverse\">\n" +
    "	           Avg Req ms\n" +
    "	            <span ng-show=\"sortType == 'avgPageimeMs' && !sortReverse\" class=\"caret\"></span>\n" +
    "	            <span ng-show=\"sortType == 'avgPageimeMs' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "			</th>\n" +
    "			<th  align=\"center\" ng-click=\"sortType = 'queryCount'; sortReverse = !sortReverse\">\n" +
    "	           Completed Queries\n" +
    "	            <span ng-show=\"sortType == 'queryCount' && !sortReverse\" class=\"caret\"></span>\n" +
    "	            <span ng-show=\"sortType == 'queryCount' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "			</th>\n" +
    "			<th  align=\"center\" ng-click=\"sortType = 'avgQueryTimeMs'; sortReverse = !sortReverse\">\n" +
    "	           Avg Query ms\n" +
    "	            <span ng-show=\"sortType == 'avgQueryTimeMs' && !sortReverse\" class=\"caret\"></span>\n" +
    "	            <span ng-show=\"sortType == 'avgQueryTimeMs' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "			</th>\n" +
    "			<th  align=\"center\" ng-click=\"sortType = 'memoryUsedMiB'; sortReverse = !sortReverse\" title=\"Memory Used\">\n" +
    "	           Memory\n" +
    "	            <span ng-show=\"sortType == 'memoryUsedMiB' && !sortReverse\" class=\"caret\"></span>\n" +
    "	            <span ng-show=\"sortType == 'memoryUsedMiB' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "			</th>\n" +
    "			<th ng-click=\"sortType = 'uptime'; sortReverse = !sortReverse\">\n" +
    "	           Uptime\n" +
    "	            <span ng-show=\"sortType == 'uptime' && !sortReverse\" class=\"caret\"></span>\n" +
    "	            <span ng-show=\"sortType == 'uptime' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "			</th>\n" +
    "			<th  align=\"center\" ng-click=\"sortType = 'aactiveRequests'; sortReverse = !sortReverse\">\n" +
    "	           Active Reqs\n" +
    "	            <span ng-show=\"sortType == 'aactiveRequests' && !sortReverse\" class=\"caret\"></span>\n" +
    "	            <span ng-show=\"sortType == 'aactiveRequests' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "			</th>\n" +
    "		</tr>\n" +
    "	<!--\n" +
    "	{\"duration\":10,\"pageCount\":0,\"memoryUsedMiB\":38,\"avgQueryTimeMs\":0,\"memoryTotalMiB\":274,\"avgPageTimeMs\":0,\"queryCount\":0,\"memoryAvailableMiB\":235,\"loadAverage\":-1,\"activeRequests\":0,\"timestamp\":1463669475566,\"uptime\":\"10:28.8\"}\n" +
    "	-->\n" +
    "		<tr ng-repeat=\"counter in counters | orderBy:sortType:sortReverse | startFrom:(currentPage-1)*pageSize | limitTo:pageSize\"> \n" +
    "			<td align=\"center\">{{counter.timestamp | date: 'MMM dd yyyy h:mm:ss a' }}</td>\n" +
    "			<td align=\"center\">{{counter.pageCount}}</td>\n" +
    "			<td align=\"center\">{{counter.avgPageTimeMs}}</td>\n" +
    "			<td align=\"center\">{{counter.queryCount}}</td>\n" +
    "			<td align=\"center\">{{counter.avgQueryTimeMs}}</td>\n" +
    "			<td align=\"center\"> \n" +
    "				<span ng-show=\"counter.memoryUsedMiB >= 2000\">{{counter.memoryUsedMiB/1000}} GB</span>\n" +
    "				<span ng-show=\"counter.memoryUsedMiB < 2000\">{{counter.memoryUsedMiB}} MB</span></td>\n" +
    "			<td align=\"center\">{{counter.uptime}}</td>\n" +
    "			<td align=\"center\">{{counter.activeRequests}}</td>\n" +
    "		</tr>\n" +
    "	</table>\n" +
    "\n" +
    "	<pagination ng-model=\"currentPage\" total-items=\"totalItems\" items-per-page=\"pageSize\" class=\"pagination-sm\" ng-show=\"noOfPages > 1\"></pagination>\n" +
    "</div>");
}]);

angular.module("dashboard/dashboard.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("dashboard/dashboard.tpl.html",
    "<div ng-show=\"isEnterprise\">\n" +
    "	<div class=\"content content-wide\">\n" +
    "		<ul class=\"nav nav-tabs\">\n" +
    "		  <li ng-class=\"{active:$state.includes('dashboard.all')}\"><a ui-sref=\"dashboard.all\">All</a></li>\n" +
    "		  <li ng-class=\"{active:$state.includes('dashboard.problems')}\"><a ui-sref=\"dashboard.problems\">Problems</a></li>\n" +
    "		  <li ng-class=\"{active:$state.includes('dashboard.incidents') && !$state.includes('dashboard.incidents.detail')}\"><a ui-sref=\"dashboard.incidents\">Incidents</a></li>\n" +
    "		  <li ng-show=\"$state.includes('dashboard.incidents.detail')\" ng-class=\"{active:$state.includes('dashboard.incidents.detail')}\"><a name=\"details\" ui-sref=\"dashboard.incidents\">Incident Details <span class=\"glyphicons remove leftMargin handcursor\"></span></a></li>\n" +
    "	\n" +
    "		</ul>\n" +
    "	</div>\n" +
    "	<div class=\"push-down\" ui-view></div>\n" +
    "</div>\n");
}]);

angular.module("dashboard/incident.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("dashboard/incident.tpl.html",
    "<div ng-hide=\"incident\" class=\"alert relative alert-info\" role=\"alert\">\n" +
    "  No data to show for this incident..\n" +
    "</div>\n" +
    "\n" +
    "<div ng-show=\"incident\">\n" +
    "\n" +
    "<div class=\"col-sm-2 text-right\">Incident Label:</div>\n" +
    "<div class=\"col-sm-10\">{{incident.incidentType}}</div>\n" +
    "\n" +
    "<div class=\"col-sm-2 text-right\">Logged at:</div>\n" +
    "<div class=\"col-sm-10\">{{incident.beginTime | date: 'MMM dd yyyy h:mm:ss a'}}</div>\n" +
    "\n" +
    "<div class=\"col-sm-2 text-right\">Number of Requests:</div>\n" +
    "<div class=\"col-sm-10\">{{incident.requests.length}}</div>\n" +
    "\n" +
    "<div class=\"row\" ng-show=\"incident.requests\">\n" +
    "	<div class=\"col-sm-2 text-right\">\n" +
    "		<h4>Logged Requests: </h4>\n" +
    "	</div> \n" +
    "	<div class=\"col-sm-10\">\n" +
    "		<div ng-repeat=\"request in incident.requests\" class=\"padded\"\n" +
    "			ng-class-odd=\"'odd'\" ng-class-even=\"'even'\">\n" +
    "\n" +
    "			<div class=\"row\" ng-show=\"request.url\">\n" +
    "				<div class=\"col-sm-2 text-right\">Request URL:</div>\n" +
    "				<div class=\"col-sm-10\"><a href=\"{{request.url}}\" target=\"top\">{{request.url}}</a></div>\n" +
    "			</div>\n" +
    "\n" +
    "			<div class=\"row\" ng-show=\"!request.url && request.requestURI\">\n" +
    "				<div class=\"col-sm-2 text-right\">Request URI:</div>\n" +
    "				<div class=\"col-sm-10\">{{request.requestURI}}</div>\n" +
    "			</div>\n" +
    "\n" +
    "			<div class=\"row\" ng-show=\"request.method\">\n" +
    "				<div class=\"col-sm-2 text-right\">HTTP Method:</div>\n" +
    "				<div class=\"col-sm-10\">{{request.method}}</div>\n" +
    "			</div>\n" +
    "\n" +
    "			<div class=\"row\" ng-show=\"request.threadName\">\n" +
    "				<div class=\"col-sm-2 text-right\">Thread Name:</div>\n" +
    "				<div class=\"col-sm-10\">{{request.threadName}}</div>\n" +
    "			</div>\n" +
    "\n" +
    "			<div class=\"row\" ng-show=\"request.time\">\n" +
    "				<div class=\"col-sm-2 text-right\">Execution Time:</div>\n" +
    "				<div class=\"col-sm-10\">{{request.time}}</div>\n" +
    "			</div>\n" +
    "\n" +
    "			<div class=\"row\" ng-show=\"request.timeToFirstByte\">\n" +
    "				<div class=\"col-sm-2 text-right\">Time to 1st Byte:</div>\n" +
    "				<div class=\"col-sm-10\">{{request.timeToFirstByte}}</div>\n" +
    "			</div>\n" +
    "\n" +
    "			<div class=\"row\" ng-show=\"request.longQueryActive\">\n" +
    "				<div class=\"col-sm-2 text-right\">Longest SQL Query:</div>\n" +
    "				<div class=\"col-sm-10\">\n" +
    "				<p>{{toDurationString(request.longQueryElapsed)}}Ms</p>\n" +
    "				<p>{{request.longQueryRows}} Rows</p>\n" +
    "				<p style=\"font-family: monospace;\">{{request.longQuerySql}}</p>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "\n" +
    "			<div class=\"row\" ng-show=\"request.stack\">\n" +
    "				<div class=\"col-sm-2 text-right\">Stack Trace:</div>\n" +
    "				<div class=\"col-sm-10\"><pre>{{request.stack}}</pre></div>\n" +
    "			</div>\n" +
    "\n" +
    "			<div class=\"row\" ng-show=\"request.ip\">\n" +
    "				<div class=\"col-sm-2 text-right\">IP Address:</div>\n" +
    "				<div class=\"col-sm-10\">{{request.ip}}</div>\n" +
    "			</div>\n" +
    "\n" +
    "			<div class=\"row\" ng-show=\"request.method\">\n" +
    "				<div class=\"col-sm-2 text-right\">HTTP Method:</div>\n" +
    "				<div class=\"col-sm-10\">{{request.method}}</div>\n" +
    "			</div>\n" +
    "		</div>\n" +
    "	</div>\n" +
    "</div>");
}]);

angular.module("dashboard/incidents.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("dashboard/incidents.tpl.html",
    "<div ui-view ng-show=\"$state.includes('dashboard.incidents.detail')\"></div>\n" +
    "\n" +
    "<div ng-hide=\"$state.includes('dashboard.incidents.detail')\">\n" +
    "\n" +
    "<div ng-show=\"initialLoading\" class=\"alert relative alert-info\">\n" +
    "	<img src=\"/img/spinner.gif\"> Loading incidents...\n" +
    "</div>\n" +
    "\n" +
    "<div ng-hide=\"initialLoading\">\n" +
    "\n" +
    "	<div ng-hide=\"incidents.length\" class=\"alert relative alert-info\" role=\"alert\">\n" +
    "	  No incidents to list right now...\n" +
    "	</div>\n" +
    "\n" +
    "	<div ng-show=\"incidents.length\">		\n" +
    "			<!-- <div class=\"row\">\n" +
    "				<div class=\"col-sm-12 text-right\"><label>Search: <input ng-model=\"searchText\"></label></div>\n" +
    "			</div> -->\n" +
    "		\n" +
    "			<table class=\"table table-striped table-hover\">\n" +
    "				<tr>\n" +
    "					<th>Label</th>\n" +
    "					<th>Start Date</th>\n" +
    "					<th>End Date</th>\n" +
    "					<th>Threshold Type</th>\n" +
    "					<th>Action Taken</th>\n" +
    "					<th></th>\n" +
    "				</tr>\n" +
    "				<!--  -->\n" +
    "				<tr ng-repeat=\"incident in incidents | startFrom:(currentPage-1)*pageSize | limitTo:pageSize\" ng-click=\"setSelectedIncident(incident.incidentID)\" ui-sref=\"dashboard.incidents.detail({id:incident.incidentID})\"  ng-class=\"{highlighted: incident.incidentID === idSelectedIncident}\"> \n" +
    "				<!-- <tr ng-repeat=\"incident in filtered = incidents | filter:searchText | startFrom:(currentPage-1)*pageSize | limitTo:pageSize\">-->\n" +
    "					<td>{{incident.incidentType}}</td>\n" +
    "					<td>{{incident.beginTime | date: 'MMM dd yyyy h:mm a'}}</td>\n" +
    "					<td>{{incident.endTime | date: 'MMM dd yyyy h:mm a'}}</td>\n" +
    "					<td>{{incident.thresholdType}} \n" +
    "					({{incident.thresholdValue}} \n" +
    "					<span ng-switch on=\"incident.thresholdType\">\n" +
    "					  <span ng-switch-when=\"ACTIVEREQ\">)</span>\n" +
    "					  <span ng-switch-when=\"\">)</span>\n" +
    "					  <span ng-switch-when=\"MEMORYPCT\">%)</span>\n" +
    "					  <span ng-switch-default>Ms)</span>\n" +
    "					</span>\n" +
    "					</td>\n" +
    "					<td>{{incident.actionTaken}}</td>\n" +
    "					<td>\n" +
    "						<span class=\"glyphbutton glyphicons remove linkish\" ng-click=\"removeIncident(incident.incidentID)\" title=\"Delete this Incident\"></span>\n" +
    "					</td>\n" +
    "					\n" +
    "				</tr>\n" +
    "			</table>\n" +
    "\n" +
    "			<pagination ng-model=\"currentPage\" total-items=\"totalItems\" items-per-page=\"pageSize\" class=\"pagination-sm\" ng-show=\"noOfPages > 1\"></pagination>\n" +
    "	</div>\n" +
    "</div>\n" +
    "</div> ");
}]);

angular.module("dashboard/servers.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("dashboard/servers.tpl.html",
    "<div ng-show=\"initialLoading\" class=\"alert relative alert-info\">\n" +
    "	<img src=\"/img/spinner.gif\"> Loading server list...\n" +
    "</div>\n" +
    "\n" +
    "<div ng-hide=\"initialLoading\">\n" +
    "	<div ng-hide=\"servers.length\" class=\"alert relative alert-info\" role=\"alert\">\n" +
    "	  No servers to list right now...\n" +
    "	</div>\n" +
    "\n" +
    "	<table class=\"table table-striped table-hover table-condensed\" ng-show=\"servers.length\">\n" +
    "	<thead>\n" +
    "		<tr>\n" +
    "			<th>Server</th>\n" +
    "			<th>Status</th>\n" +
    "			<th>Uptime</th>\n" +
    "			<th width=\"100\">Memory</th>\n" +
    "			<th width=\"100\">Active Pages</th>\n" +
    "			<th width=\"100\">Pages/Sec</th>\n" +
    "			<th width=\"100\">Qrys/Sec</th>\n" +
    "			<th width=\"100\">Avg Page Ms</th>\n" +
    "			<th width=\"100\">Avg Qry Ms</th>\n" +
    "		</tr>\n" +
    "	</thead>\n" +
    "	<tbody>\n" +
    "		<tr ng-repeat=\"server in servers\" ng-click=\"openServer(server);\">\n" +
    "			<td>{{server.name}}</td>\n" +
    "			<td>{{server.status}}</td>\n" +
    "			<td>{{server.uptime}}</td>\n" +
    "			<td><progress-bar cur=\"$parent.server.memory.used\" max=\"$parent.server.memory.total\" title=\"{{mb($parent.server.memory.used)}}M / {{mb($parent.server.memory.total)}}M\">{{(server.memory.used/server.memory.total*100) | number:1}}%</progress-bar>\n" +
    "			</td>\n" +
    "			<td><progress-bar cur=\"$parent.server.numcurrentrequests\" max=\"$parent.server.maxcurrentrequests\">{{server.numcurrentrequests}}/{{server.maxcurrentrequests}}</progress-bar></td>\n" +
    "			<td><progress-bar cur=\"$parent.server.pps\" max=\"100\">{{server.pps | number:1}}</progress-bar></td>\n" +
    "			<td><progress-bar cur=\"$parent.server.qps\" max=\"1000\">{{server.qps | number:1}}</progress-bar></td>\n" +
    "			<td><progress-bar cur=\"$parent.server.avgpagetime\" max=\"$parent.server.slowpagethreshold\" title=\"{{$parent.server.avgpagetime}}/{{$parent.server.slowpagethreshold}}\">{{server.avgpagetime | number}}</progress-bar></td>\n" +
    "			<td><progress-bar cur=\"$parent.server.avgqrytime\" max=\"$parent.server.slowquerythreshold\" title=\"{{$parent.server.avgqrytime}}/{{$parent.server.slowqrythreshold}}\">{{server.avgqrytime | number}}</progress-bar></td>\n" +
    "		</tr>\n" +
    "	</tbody>\n" +
    "	</table>\n" +
    "</div>");
}]);

angular.module("dos/dos.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("dos/dos.tpl.html",
    "<div class=\"container-fluid\">\n" +
    "	<div class=\"row\">\n" +
    "		<div class=\"col-sm-12 col-md-6 col-lg-8\" ng-include=\"'dos/settings.tpl.html'\"></div>\n" +
    "		<div class=\"col-sm-12 col-md-6 col-lg-4\" ng-include=\"'dos/list.tpl.html'\"></div>\n" +
    "	</div>\n" +
    "</div>");
}]);

angular.module("dos/list.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("dos/list.tpl.html",
    "<div class=\"panel panel-default\" ng-show=\"addingExclusion\">\n" +
    "	<div class=\"panel-heading\">\n" +
    "		<h3 class=\"panel-title\">Add an IP Addresses</h3>\n" +
    "	</div>\n" +
    "	<div class=\"content panel-body\">\n" +
    "		<form class=\"form-horizontal\" name=\"dos_exclusion\"> \n" +
    "			<p>This IP address will <u>never</u> get banned</p>\n" +
    "			<input type=\"text\" id=\"ip\" name=\"ip\" ng-model=\"newExclusion.ip\" placeholder=\"New IP Address\" ng-pattern='/^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/'>\n" +
    "			<button class=\"btn btn-sm btn-primary\" type=\"button\" ng-click=\"insertExclusion()\">Add IP</button>\n" +
    "			<button class=\"btn btn-sm btn-primary\" type=\"button\" ng-click=\"closeExclusion()\">Cancel</button>\n" +
    "		</form>\n" +
    "	</div>\n" +
    "</div>\n" +
    "\n" +
    "\n" +
    "<div class=\"nopadding panel panel-default\">\n" +
    "	<div class=\"panel-heading\">\n" +
    "		<h3 class=\"panel-title\">Banned IP Addresses</h3>\n" +
    "	</div>\n" +
    "	<div class=\"content panel-body\">\n" +
    "		<tabset class=\"config\">\n" +
    "			<tab ng-repeat=\"ipList in blockLists\" heading=\"{{ipList.label}}\" data-spy=\"scroll\" active=\"ipList.isActive\">\n" +
    "				<a name=\"{{ipList.label}}\"></a>\n" +
    "				<div class=\"table-responsive\">\n" +
    "					<table class=\"table table-striped table-hover\">\n" +
    "						<tbody>\n" +
    "							<tr>\n" +
    "								<th>{{ipList.hint}}</th>\n" +
    "								<th ng-show=\"ipList.isExclude\"><span class=\"glyphbutton glyphicons plus linkish\" ng-click=\"addExclusion()\" title=\"Add an exclusion IP address\"></span></th>\n" +
    "							</tr>\n" +
    "							<tr ng-repeat=\"ip in dosStatus[ipList.element]\">\n" +
    "								<td>{{ip}}</td>\n" +
    "								<td>\n" +
    "									<span class=\"glyphbutton glyphicons remove linkish\" ng-click=\"removeIP(ipList.element,ip)\" title=\"Remove this IP\"></span>\n" +
    "								</td>\n" +
    "							</tr>\n" +
    "							<tr ng-show=\"dosStatus[ipList.element].length==0\">\n" +
    "								<td colspan=\"2\"><div class=\"alert relative alert-info\" role=\"alert\">No IPs Recorded</div></td>\n" +
    "							</tr>\n" +
    "						</tbody>\n" +
    "					</table>\n" +
    "				</div>\n" +
    "			</tab>\n" +
    "		</tabset>\n" +
    "	</div>\n" +
    "</div>\n" +
    "");
}]);

angular.module("dos/settings.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("dos/settings.tpl.html",
    "<div class=\"nopadding panel panel-default\">\n" +
    "<div class=\"panel-heading\">\n" +
    "	<h3 class=\"panel-title\">Settings</h3>\n" +
    "</div>\n" +
    "<div class=\"content panel-body\">\n" +
    "	<form class=\"form-horizontal\" name=\"dos_settings\"> \n" +
    "		<table class=\"table table-striped table-hover\">\n" +
    "			<tbody>\n" +
    "				<tr>\n" +
    "					<td class=\"col-sm-4\"><strong>DOS Protection Enabled?</strong></td>\n" +
    "					<td class=\"col-sm-8\">\n" +
    "						<toggle-switch class=\"switch-success\" ng-model=\"dosConfig.enabled\" on-label=\"Enabled\" off-label=\"Disabled\" ng-value=\"dosConfig.enabled\"></toggle-switch>\n" +
    "						<p class=\"hint\">\n" +
    "							Turn Denial of Service Protection On/Off\n" +
    "						</p>\n" +
    "					</td>\n" +
    "				</tr>\n" +
    "				<tr>\n" +
    "					<td class=\"col-sm-4\"><strong>Request Limit</strong></td>\n" +
    "					<td class=\"col-sm-8\">\n" +
    "						<input name=\"requestlimit\" numericbinding type=\"number\" class=\"input-mini\" ng-model=\"dosConfig.requestlimit\" id=\"requestlimit\"> \n" +
    "						<p class=\"hint\">\n" +
    "							Number of concurrent requests to trigger a ban (from the same IP)\n" +
    "						</p>\n" +
    "					</td>\n" +
    "				</tr>\n" +
    "				<tr>\n" +
    "					<td class=\"col-sm-4\"><strong>Ban Duration</strong></td>\n" +
    "					<td class=\"col-sm-8\">\n" +
    "						<input name=\"bandurationmin\" numericbinding type=\"number\" class=\"input-mini\" ng-model=\"dosConfig.bandurationmin\" id=\"bandurationmin\"> minutes\n" +
    "						<p class=\"hint\">\n" +
    "							Duration of a temporary ban (minutes)\n" +
    "						</p>\n" +
    "					</td>\n" +
    "				</tr>\n" +
    "				<tr>\n" +
    "					<td class=\"col-sm-4\"><strong>Protection Action</strong></td>\n" +
    "					<td class=\"col-sm-8\">\n" +
    "						<div ng-repeat=\"option in protectionOptions\">\n" +
    "						<label><input type=\"radio\" ng-model=\"dosConfig.action\" ng-value=\"option.element\"> {{option.label}}: </label>\n" +
    "						{{option.hint}}\n" +
    "						</div>\n" +
    "					</td>\n" +
    "				</tr>\n" +
    "				<tr>\n" +
    "					<td class=\"col-sm-4\"> </td>\n" +
    "					<td class=\"col-sm-8\" ng-switch=\"item.type\"><button class=\"btn btn-sm btn-primary\" type=\"button\" ng-click=\"saveConfig()\">Save Settings</button></td>\n" +
    "				</tr>\n" +
    "			</tbody>\n" +
    "		</table>\n" +
    "	</form>\n" +
    "</div>\n" +
    "</div>");
}]);

angular.module("header.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("header.tpl.html",
    "<div class=\"container-fluid\">\n" +
    "	<div class=\"row data-widgets\" style=\"margin-top:5px;\">\n" +
    "		<div class=\"col-sm-12 col-md-4 col-lg-4\" ng-if=\"isAuthenticated\">\n" +
    "			<sf-metric-widget metrics=\"serverMetrics\" showcontrols=\"true\" title=\"serverNameTitle\" gc=\"gc()\" refresh=\"refresh()\" setPodRefresh=\"setpodrefresh(sec)\" \n" +
    "			refreshTime=\"podtimeout\"></sf-metric-widget>\n" +
    "		</div>\n" +
    "		<div class=\"col-sm-12 col-md-4 col-lg-4\" ng-if=\"isAuthenticated\">\n" +
    "			<sf-metric-widget metrics=\"requestMetrics\" title=\"'Requests'\"></sf-metric-widget>\n" +
    "		</div>\n" +
    "		<div class=\"col-sm-12 col-md-4 col-lg-4\" ng-if=\"isAuthenticated\">\n" +
    "			<sf-metric-widget metrics=\"queryMetrics\" title=\"'Queries'\"></sf-metric-widget>\n" +
    "		</div>\n" +
    "		<!-- <div class=\"hidden-xs hidden-sm hidden-md hidden-lg text-muted\" ng-if=\"isAuthenticated\">\n" +
    "			<span ng-show=\"hasPendingRequests()\" class=\"glyphicons heart\"></span>\n" +
    "			<span ng-hide=\"hasPendingRequests()\" class=\"glyphicons heart_empty\"></span>\n" +
    "		</div> -->\n" +
    "	</div>\n" +
    "</div>\n" +
    "\n" +
    "<div class=\"breadcrumb\" style=\"position:relative;\">{{breadcrumb}} <img src=\"SeeFusion.png\" class=\"logo\" border=\"0\" style=\"position:absolute;right:0px;top:0px;\"/></div>\n" +
    "\n" +
    "<!-- <button type=\"btn\" ng-show=\"!showNav\" ng-click=\"toggleNav()\"><span class=\"glyphicons resize_full\"></span> Show Menu</button> -->\n" +
    "\n" +
    "<div class=\"push-down\">\n" +
    "	<alert ng-repeat=\"alert in alerts\" type=\"{{alert.type}}\" close=\"closeAlert(alert)\">{{ alert.msg }}</alert>\n" +
    "</div>");
}]);

angular.module("help/help.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("help/help.tpl.html",
    "");
}]);

angular.module("info/connection-modal.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("info/connection-modal.tpl.html",
    "<div class=\"modal-header\">\n" +
    "	<h3 class=\"modal-title\">Connection Problem</h3>\n" +
    "</div>\n" +
    "<div class=\"modal-body\">\n" +
    "\n" +
    "	Connection Problem...\n" +
    "\n" +
    "</div>");
}]);

angular.module("info/connectivity.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("info/connectivity.tpl.html",
    "<div class=\"col-xs-2\">\n" +
    "<span class=\"glyphicons certificate grey\" style=\"font-size:200px\"></span>\n" +
    "</div>\n" +
    "<div class=\"col-xs-10\" style=\"margin-top:2em;\">\n" +
    "<p>\n" +
    "We're having a problem connecting to your server.\n" +
    "</p><p>\n" +
    "Please make sure your application server is running.\n" +
    "</p>\n" +
    "\n" +
    "<p><button class=\"btn btn-sm btn-primary\" type=\"button\" ng-click=\"retry()\">Retry Connection</button></p>\n" +
    "</div>");
}]);

angular.module("info/info.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("info/info.tpl.html",
    "<div ui-view></div>");
}]);

angular.module("info/license.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("info/license.tpl.html",
    "");
}]);

angular.module("log/log.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("log/log.tpl.html",
    "<div class=\"panel\">\n" +
    "	<div class=\"content\">\n" +
    "		<pre>{{log}}</pre>\n" +
    "	</div>\n" +
    "</div>");
}]);

angular.module("login/login-modal.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("login/login-modal.tpl.html",
    "<div class=\"modal-header\">\n" +
    "	<h3 class=\"modal-title\">Login</h3>\n" +
    "</div>\n" +
    "<div class=\"modal-body\">\n" +
    "\n" +
    "	<form class=\"form-horizontal\" name=\"form\" novalidate ng-submit=\"login()\">\n" +
    "\n" +
    "		<div class=\"form-group has-feedback\" ng-class=\"{'has-error':form.password.$invalid && form.password.$dirty, 'has-success':form.password.$valid}\">\n" +
    "			<label for=\"password\" class=\"col-sm-2 control-label\">Password</label>\n" +
    "			<div class=\"col-sm-4\">\n" +
    "				<input id=\"loginpassword\" type=\"password\" class=\"form-control\" name=\"password\" placeholder=\"Password\" required ng-model=\"password\">\n" +
    "				<span class=\"glyphicons ok form-control-feedback\"></span>\n" +
    "			</div>\n" +
    "		</div>\n" +
    "\n" +
    "	<form>\n" +
    "\n" +
    "</div>\n" +
    "<div class=\"modal-footer\">\n" +
    "	<button ng-disabled=\"form.$invalid\" type=\"submit\" class=\"btn btn-primary\" ng-click=\"login()\"><span class=\"glyphicons white check\"></span>Login</button>\n" +
    "	<button class=\"btn btn-default\" ng-click=\"cancel()\">Cancel</button>\n" +
    "</div>\n" +
    "");
}]);

angular.module("monitoring/edit.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("monitoring/edit.tpl.html",
    "<form class=\"form-horizontal\" name=\"ruleForm\">\n" +
    "	<div class=\"form-group\" ng-class=\"{'has-error' : ruleForm.inputName.$dirty && ruleForm.inputName.$invalid}\">\n" +
    "		<label class=\"col-sm-2 control-label\" for=\"inputName\">Name</label>\n" +
    "		<div class=\"col-sm-8\">\n" +
    "			<input type=\"text\" id=\"inputName\" name=\"inputName\" placeholder=\"Rule Name...\" ng-model=\"rule.name\" ng-required=\"inputName\" ng-pattern=\"/^[a-zA-Z0-9 ]*$/\" class=\"form-control\" />\n" +
    "			<div role=\"alert\" class=\"errorHighlight\" ng-show=\"ruleForm.inputName.$dirty\">\n" +
    "      		<span  ng-show=\"ruleForm.inputName.$error.required\">Please enter a rule name</span>\n" +
    "      		<span  ng-show=\"ruleForm.inputName.$error.pattern\">Rule name can only contain letters, numbers and spaces</span>\n" +
    "    		</div>\n" +
    "		</div>\n" +
    "	</div>\n" +
    "\n" +
    "	<div class=\"form-group\">\n" +
    "		<label class=\"col-sm-2 control-label\" for=\"isEnabled\">Enabled</label>\n" +
    "		<div class=\"col-sm-8 controls\">\n" +
    "			<toggle-switch class=\"switch-success\" ng-model=\"rule.isEnabled\" on-label=\"Enabled\" off-label=\"Disabled\"></toggle-switch>\n" +
    "		</div>\n" +
    "	</div>\n" +
    "\n" +
    "	<div class=\"form-group\" ng-class=\"{'has-error' : ruleForm.inputtriggerType.$dirty && ruleForm.inputtriggerType.$invalid}\">\n" +
    "		<label class=\"col-sm-2 control-label\" for=\"inputtriggerType\">Triggered By</label>\n" +
    "		<div class=\"col-sm-8\">\n" +
    "			<select id=\"inputtriggerType\" name=\"inputtriggerType\" ng-model=\"rule.triggerType\" ng-options=\"r.value as r.label for r in ruleTypes\" ng-required class=\"form-control\">\n" +
    "				<option value=\"\">Select a Trigger Type</option>\n" +
    "			</select>\n" +
    "			<div role=\"alert\" class=\"errorHighlight\" ng-show=\"ruleForm.inputtriggerType.$dirty\">\n" +
    "      		<span ng-show=\"ruleForm.inputtriggerType.$error.required\">Please choose a trigger type</span>\n" +
    "    		</div>\n" +
    "		</div>\n" +
    "	</div>\n" +
    "\n" +
    "	<div class=\"form-group\" ng-class=\"{'has-error' : ruleForm.inputtriggerLimit.$dirty && ruleForm.inputtriggerLimit.$invalid}\">\n" +
    "		<label class=\"col-sm-2 control-label\" for=\"inputtriggerLimit\">Threshold</label>\n" +
    "		<div class=\"col-sm-8 controls\">\n" +
    "			<input type=\"number\" id=\"inputtriggerLimit\" name=\"inputtriggerLimit\" ng-model=\"rule.triggerLimit\" class=\"input-mini\" ng-pattern=\"/^[0-9]*$/\" ng-required>\n" +
    "			<span ng-switch on=\"rule.triggerType\">\n" +
    "			  <span ng-switch-when=\"ACTIVEREQ\"> </span>\n" +
    "			  <span ng-switch-when=\"\"> </span>\n" +
    "			  <span ng-switch-when=\"MEMORYPCT\">%</span>\n" +
    "			  <span ng-switch-default>Ms</span>\n" +
    "			</span>	\n" +
    "			<span class=\"help-block\">This is the threshold at which this rule is triggered</span> \n" +
    "			<div role=\"alert\" class=\"errorHighlight\" ng-show=\"ruleForm.inputtriggerLimit.$dirty\">\n" +
    "      			<span ng-show=\"ruleForm.inputtriggerLimit.$invalid\">The threshold value must be a number</span>\n" +
    "    		</div>\n" +
    "		</div>\n" +
    "\n" +
    "	</div>\n" +
    "\n" +
    "	<div class=\"form-group\">\n" +
    "		<label class=\"col-sm-2 control-label\" for=\"inputexcludeURLs\">Exclude URLs</label>\n" +
    "		<div class=\"col-sm-8 controls\">\n" +
    "			<!-- <div ng-repeat=\"u in rule.excludeURLs\">\n" +
    "				<span class=\"glyphbutton glyphicons remove\" ng-click=\"removeExclude(u)\" title=\"Remove this exclusion URL\"></span>\n" +
    "				<span ng-bind=\"u\"></span>\n" +
    "			</div>\n" +
    "			<div class=\"controls\">\n" +
    "				<input type=\"text\" ng-model=\"newExclude\">\n" +
    "				<button class=\"btn btn-sm btn-primary\" ng-click=\"addExclude()\">Add</button>\n" +
    "			</div> -->\n" +
    "			<div class=\"controls\">\n" +
    "				<textarea id=\"inputexcludeURLs\" name=\"inputexcludeURLs\" split-array ng-model=\"rule.excludeURLs\" class=\"form-control\" rows=5></textarea>\n" +
    "			</div>\n" +
    "			<span class=\"help-block\">Request-based rules that match these substrings will be ignored (one per line)</span>\n" +
    "		</div>\n" +
    "	</div>\n" +
    "\n" +
    "	<div class=\"form-group\">\n" +
    "		<label class=\"col-sm-2 control-label\" for=\"inputDelay\">Delay</label>\n" +
    "		<div class=\"col-sm-8 controls\">\n" +
    "			<input type=\"number\" id=\"inputDelay\" placeholder=\"zzz\" ng-model=\"rule.delay\" class=\"input-mini\" ng-pattern=/^[0-9]*$/> seconds\n" +
    "			<span class=\"help-block\">This is the amount of time the rule must be \"active\" before action or notification</span>\n" +
    "		</div>\n" +
    "	</div>\n" +
    "\n" +
    "	<div class=\"form-group\">\n" +
    "		<label class=\"col-sm-2 control-label\" for=\"isKillResponse\">Action</label>\n" +
    "		<div class=\"col-sm-8 controls\">\n" +
    "			<!-- <input type=\"checkbox\" id=\"isLogResponse\" ng-model=\"rule.isLogResponse\"><label for=\"isLogResponse\"> Log Event</label><br/> --> <!-- DB Took this out, but not sure if that's going to be permanent -->\n" +
    "			<input type=\"checkbox\" id=\"isKillResponse\" ng-model=\"rule.isKillResponse\"> <label for=\"isKillResponse\"> Kill (Oldest) Page</label>\n" +
    "		</div>\n" +
    "	</div>\n" +
    "	\n" +
    "	<div class=\"form-group\">\n" +
    "		<label class=\"col-sm-2 control-label\" for=\"inputsleep\">Sleep</label>\n" +
    "		<div class=\"col-sm-8 controls\">\n" +
    "			<span><input type=\"number\" id=\"inputsleep\" placeholder=\"zzzz\" ng-model=\"rule.sleep\" class=\"input-mini\" ng-pattern=/^[0-9]*$/ > minutes</span>\n" +
    "			<span class=\"help-block\">This is the delay between repeated triggers of this rule</span>\n" +
    "		</div>\n" +
    "	</div>\n" +
    "\n" +
    "	<div class=\"form-group\"> \n" +
    "		<label class=\"col-sm-2 control-label\" for=\"isNotifyResponse\">Notify</label>\n" +
    "		<div class=\"col-sm-8 controls\">\n" +
    "			<input type=\"checkbox\" id=\"isNotifyResponse\" ng-model=\"rule.isNotifyResponse\"> <label for=\"isNotifyResponse\"> Send Notification</label>\n" +
    "		</div>\n" +
    "	</div>\n" +
    "\n" +
    "	<div class=\"form-group\" ng-show=\"rule.isNotifyResponse\">\n" +
    "		<label class=\"col-sm-2 control-label\" for=\"inputsmtpFrom\">Mail From:</label>\n" +
    "		<div class=\"col-sm-8 controls\">\n" +
    "			<input type=\"text\" id=\"inputsmtpFrom\" placeholder=\"alerts@mydomain.com\" ng-model=\"rule.smtpFrom\">\n" +
    "		</div>\n" +
    "	</div>\n" +
    "\n" +
    "	<div class=\"form-group\" ng-show=\"rule.isNotifyResponse\">\n" +
    "		<label class=\"col-sm-2 control-label\" for=\"inputsmtpTo\">Send To:</label>\n" +
    "		<div class=\"col-sm-8 controls\">\n" +
    "			<input type=\"text\" id=\"inputsmtpTo\" placeholder=\"name@mydomain.com,name2@mydomain.com...\" ng-model=\"rule.smtpTo\">\n" +
    "			<span class=\"help-block\">Whitespace- or comma-delimited list</span>\n" +
    "		</div>\n" +
    "	</div>\n" +
    "\n" +
    "	<div class=\"form-group\" ng-show=\"rule.isNotifyResponse\">\n" +
    "		<label class=\"col-sm-2 control-label\" for=\"inputsmtpSubject\">Subject</label>\n" +
    "		<div class=\"col-sm-8 controls\">\n" +
    "			<input type=\"text\" id=\"inputsmtpSubject\" placeholder=\"The sky is falling..!\" ng-model=\"rule.smtpSubject\">\n" +
    "			<span class=\"help-block\">\n" +
    "				You can include the following variables in the Subject of the message:<br> \n" +
    "				{name} : The name of the rule.<br>\n" +
    "				{reason} : The reason the rule was hit (for example, \"Current Request Count 15 >= limit of 15\")<br>\n" +
    "				{action} : The action(s) performed.<br>\n" +
    "				<!--{uri} : The request that triggered this rule (or the oldest active request for Server rules)<br>\n" +
    "				{query} : Information about the currently active query in the [oldest] active page.<br> -->\n" +
    "			</span>\n" +
    "					\n" +
    "		</div>\n" +
    "	</div>\n" +
    "\n" +
    "	<div class=\"form-group\" ng-show=\"rule.isNotifyResponse\">\n" +
    "		<label class=\"col-sm-2 control-label\" for=\"inputnotifyDisableMin\">notifyDisableMin</label>\n" +
    "		<div class=\"col-sm-8 controls\">\n" +
    "			<input type=\"number\" id=\"inputnotifyDisableMin\" placeholder=\"\" ng-model=\"rule.notifyDisableMin\" class=\"input-mini\" ng-pattern=/^[0-9]*$/>min\n" +
    "			<span class=\"help-block\">This is the amount of that must elapse between notifications.</span>\n" +
    "		</div>\n" +
    "	</div>\n" +
    "	<div class=\"form-group\">\n" +
    "		<div class=\"col-sm-8 controls\">\n" +
    "			<button type=\"submit\" class=\"btn btn-primary\" ng-click=\"submitted=true;save();\" href=\"#\" onClick=\"return false;\">Save</button>\n" +
    "			<button type=\"submit\" class=\"btn btn-default\" ui-sref=\"monitoring.rules\">Cancel</button>\n" +
    "			<img src=\"images/loading.gif\" ng-show=\"saveBusy\" />\n" +
    "			<img src=\"images/messages/error.png\" ng-show=\"saveNotOK\" />{{saveErr}}\n" +
    "		</div>\n" +
    "	</div>\n" +
    "</form>");
}]);

angular.module("monitoring/incidents.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("monitoring/incidents.tpl.html",
    "<div class=\"col-md-7\" ng-show=\"incidents\">\n" +
    "			\n" +
    "		<div class=\"row\">\n" +
    "			<div class=\"col-sm-6 text-right\"><label>Search: <input ng-model=\"searchText\"></label></div>\n" +
    "		</div>\n" +
    "	\n" +
    "		<table class=\"table table-striped table-hover\">\n" +
    "			<tr>\n" +
    "				<th>Label</th>\n" +
    "				<th>Start Date</th>\n" +
    "				<th>End Date</th>\n" +
    "				<th></th>\n" +
    "				<th></th>\n" +
    "			</tr>\n" +
    "			<!-- <tr ng-repeat=\"profile in status['saved-server-profiles']\"> -->\n" +
    "			<tr ng-repeat=\"incident in incidents.slice(((currentPage-1)*pageSize), ((currentPage)*pageSize)) | filter:searchText track by $index \">\n" +
    "				<td  ng-click=\"profile({id:profile.id})\">{{profile.name}}</td>\n" +
    "				<td>{{profile.startTime | date: 'MMM dd yyyy h:mm a'}}</td>\n" +
    "				<td>{{profile.endTime | date: 'MMM dd yyyy h:mm a'}}</td>\n" +
    "				<td>\n" +
    "					<a ui-sref=\"incident({id:incident.id})\"><span class=\"glyphbutton glyphicons list\" title=\"Review this Incident\"></span></a>\n" +
    "				</td>\n" +
    "				<td>\n" +
    "					<a ui-sref=\"removeIncident({id:incident.id})\"><span class=\"glyphbutton glyphicons remove\" ng-click=\"removeIncident({id:incident.id})\" title=\"Delete this Incident\"></span></a>\n" +
    "				</td>\n" +
    "				\n" +
    "			</tr>\n" +
    "			<tr ng-show=\"incidents.length==0\">\n" +
    "				<td colspan=\"6\">No Incidents Recorded.</td>\n" +
    "			</tr>\n" +
    "		</table>\n" +
    "\n" +
    "		<pagination ng-show=\"status['saved-server-profiles'].length > pageSize\" ng-model=\"currentPage\" total-items=\"status['saved-server-profiles'].length\" items-per-page=\"pageSize\" class=\"pagination-sm\"></pagination>\n" +
    "\n" +
    "</div>");
}]);

angular.module("monitoring/monitoring.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("monitoring/monitoring.tpl.html",
    "<div class=\"panel\">\n" +
    "	<div ui-view></div>\n" +
    "</div>");
}]);

angular.module("monitoring/rules.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("monitoring/rules.tpl.html",
    "<table class=\"table table-striped table-hover table-condensed results\">\n" +
    "	<tr>\n" +
    "		<th>\n" +
    "			<span class=\"glyphicons heart\" style=\"visibility: hidden;\"></span>\n" +
    "			Name\n" +
    "		</th>\n" +
    "		<th>Type</th>\n" +
    "		<th>Threshold</th>\n" +
    "		<th>Logs</th>\n" +
    "		<th>Kills</th>\n" +
    "		<th>Notifies</th>\n" +
    "		<th></th>\n" +
    "	</tr>\n" +
    "	<tr ng-repeat=\"rule in rules\">\n" +
    "		<td ng-class=\"{disabled: !rule.isEnabled}\" ng-click=\"editRule(rule)\">\n" +
    "			<span class=\"glyphicons circle_ok green\" style=\"color:green;\" ng-show = \"rule.isEnabled\"></span>\n" +
    "			<span class=\"glyphicons circle_exclamation_mark red\" style=\"color:red;\" ng-hide = \"rule.isEnabled\"></span>\n" +
    "			{{rule.name}}\n" +
    "		</td>\n" +
    "		<td>{{rule.triggerTypeString}}</td>\n" +
    "		<td>{{rule.triggerLimit}}\n" +
    "		<span ng-switch on=\"rule.triggerType\">\n" +
    "		  <span ng-switch-when=\"ACTIVEREQ\"> </span>\n" +
    "		  <span ng-switch-when=\"\"> </span>\n" +
    "		  <span ng-switch-when=\"MEMORYPCT\">%</span>\n" +
    "		  <span ng-switch-default>Ms</span>\n" +
    "		</span>	\n" +
    "		</td>\n" +
    "		<td>{{rule.isLogResponse}}</td>\n" +
    "		<td>{{rule.isKillResponse}}</td>\n" +
    "		<td>{{rule.isNotifyResponse}}</td>\n" +
    "		<td>\n" +
    "			<span class=\"glyphbutton glyphicons remove\" ng-click=\"removeRule(rule)\" title=\"Delete this rule\"></span>\n" +
    "		</td>\n" +
    "	</tr>\n" +
    "	<tr ng-show=\"rules.length==0\">\n" +
    "		<td colspan=\"10\">No rules defined.</td>\n" +
    "	</tr>\n" +
    "</table>\n" +
    "<button ng-click=\"newRule()\" class=\"btn btn-primary\" type=\"button\">New Rule</button>");
}]);

angular.module("nav.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("nav.tpl.html",
    "<ul  class=\"nav nav-pills nav-stacked\" ng-if=\"isAuthenticated\">\n" +
    "    <li title=\"SeeFusion\">\n" +
    "        <img src=\"SeeFusionInv.png\" alt=\"SeeFusion\" width=\"180\" border=\"0\"/>\n" +
    "    </li>\n" +
    "    <!-- ng-if=\"isAuthenticated\" -->\n" +
    "    <!-- Server --> \n" +
    "    <li ui-sref=\"server.active\" title=\"Server\" ng-class=\"$state.includes('server') ? 'icon_link' : 'white'\">\n" +
    "        <span class=\"glyphicons imac\"></span> <span class=\"navText\">Server</span>\n" +
    "    </li>\n" +
    "    <!-- Dashboard -->\n" +
    "    <li ng-show=\"isEnterprise\" ui-sref=\"dashboard.all\" title=\"Dashboard\" ng-class=\"$state.includes('dashboard') ? 'icon_link' : 'white'\">\n" +
    "        <span class=\"glyphicons more_windows\"></span> <span class=\"navText\">Dashboard</span>\n" +
    "    </li>\n" +
    "    <!-- Counters -->\n" +
    "    <li ui-sref=\"counters\" title=\"Counters\" ng-class=\"$state.includes('counters') ? 'icon_link' : 'white'\">\n" +
    "        <span class=\"glyphicons cardio\"></span> <span class=\"navText\">Counters</span>\n" +
    "    </li>\n" +
    "    <!-- Configuration -->\n" +
    "    <li ui-sref=\"config\" title=\"Configuration\" ng-class=\"$state.includes('config') ? 'icon_link' : 'white'\"><span class=\"glyphicons settings\"></span> <span class=\"navText\">Configuration</span></li>\n" +
    "    <!-- Active Monitoring -->\n" +
    "    <li ui-sref=\"monitoring.rules\" title=\"Active Monitoring\" ng-class=\"$state.includes('monitoring') ? 'icon_link' : 'white'\"><span class=\"glyphicons group\"></span> <span class=\"navText\">Active Monitoring</span></li>\n" +
    "    <!-- Stack -->\n" +
    "    <li ui-sref=\"stack.seestack({threadname:'',requestid:''})\" title=\"Stack\" ng-class=\"$state.includes('stack') ? 'icon_link' : 'white'\"><span class=\"glyphicons show_lines\"></span> <span class=\"navText\">Stack</span></li>\n" +
    "    <!-- Profiling -->\n" +
    "    <li ui-sref=\"profiling\" title=\"Profiling\" ng-class=\"$state.includes('profiling') || $state.includes('profile') ? 'icon_link' : 'white'\"><span class=\"glyphicons record\"></span> <span class=\"navText\">Profiling</span></li>\n" +
    "    <!-- DOS Protection -->\n" +
    "    <li ui-sref=\"dos\" title=\"DOS Protection\" ng-class=\"$state.includes('dos') ? 'icon_link' : 'white'\"><span class=\"glyphicons shield\"></span> <span class=\"navText\">DOS Protection</span></li>\n" +
    "    <!-- Logging -->\n" +
    "    <li ui-sref=\"log\" title=\"Log\" ng-class=\"$state.includes('log') ? 'icon_link' : 'white'\"><span class=\"glyphicons blog\"></span> <span class=\"navText\">Logs</span></li>\n" +
    "    <!-- About -->\n" +
    "    <li ui-sref=\"about\" title=\"About\" ng-class=\"$state.includes('about') ? 'icon_link' : 'white'\"><span class=\"glyphicons circle_question_mark\"></span> <span class=\"navText\">About SeeFusion</span></li>\n" +
    "    \n" +
    "    <li ng-click=\"logout()\" title=\"Logout\" class=\"white\"><span class=\"glyphicons log_out\"></span> <span class=\"navText\">Log Out</span></li>\n" +
    "    <li ng-click=\"toggleNav()\" class=\"white\"><span class=\"glyphicons resize_small\"></span> <span class=\"navText\">Toggle Nav</span></li>\n" +
    "</ul> \n" +
    "");
}]);

angular.module("navExpand.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("navExpand.tpl.html",
    "<ul class=\"nav nav-pills nav-stacked\">\n" +
    "    <li ng-click=\"toggleNav()\" class=\"white\"><span class=\"glyphicons resize_full\"></span></li>\n" +
    "</ul> ");
}]);

angular.module("notifications.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("notifications.tpl.html",
    "<div ng-class=\"['alert', 'alert-'+notification.type]\" ng-repeat=\"notification in notifications.getCurrent()\">\n" +
    "    <button class=\"close\" ng-click=\"removeNotification(notification)\">x</button>\n" +
    "    <span ng-if=\"notification.type === 'success'\" class=\"glyphicon glyphicon-thumbs-up\"></span> \n" +
    "    {{notification.message}}\n" +
    "</div>\n" +
    "");
}]);

angular.module("popup.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("popup.tpl.html",
    "<!-- Modal content-->\n" +
    "<div class=\"modal-content\">\n" +
    "  <div class=\"modal-header\">\n" +
    "    <button type=\"button\" class=\"close\" ng-click=\"cancel()\">&times;</button>\n" +
    "    <h4 class=\"modal-title\"><br/></h4>\n" +
    "  </div>\n" +
    "  <div class=\"modal-body\">\n" +
    "    <p>{{message}}</p>\n" +
    "  </div>\n" +
    "  <div class=\"modal-footer\">\n" +
    "    <button class=\"btn btn-default\" ng-click=\"cancel()\">Close</button>\n" +
    "  </div>\n" +
    "</div>");
}]);

angular.module("problemHeader.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("problemHeader.tpl.html",
    "<div class=\"problemTitle\">{{breadcrumb}}<img src=\"SeeFusion.png\" border=\"0\" class=\"pull-right\"/></div>\n" +
    "");
}]);

angular.module("profiling/profile.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("profiling/profile.tpl.html",
    "<div class=\"container-fluid\">\n" +
    "	<div class=\"row\">\n" +
    "		<div class=\"col-md-5\">\n" +
    "			<div class=\"panel panel-default\">\n" +
    "				<div class=\"panel-heading\">Profile Summary</div>\n" +
    "				<div class=\"panel-body\">\n" +
    "					<div class=\"container-fluid\">\n" +
    "						<div class=\"row\">\n" +
    "							<div class=\"col-sm-5 text-right\">Profile Label:</div>\n" +
    "							<div class=\"col-sm-7\">{{profileInfo.name}}</div>\n" +
    "						</div>\n" +
    "						<div class=\"row\">\n" +
    "							<div class=\"col-sm-5 text-right\">Recorded On:</div>\n" +
    "							<div class=\"col-sm-7\">{{profileInfo.startTick | date: 'MMM dd yyyy h:mm a'}}</div>\n" +
    "						</div>\n" +
    "						<div class=\"row\">\n" +
    "							<div class=\"col-sm-5 text-right\">Profile Duration:</div>\n" +
    "							<div class=\"col-sm-7\">{{profileInfo.actualDurationMs | millSecondsToTimeString}}</div>\n" +
    "						</div>\n" +
    "						<div class=\"row\">\n" +
    "							<div class=\"col-sm-5 text-right\">Recording Interval:</div>\n" +
    "							<div class=\"col-sm-7\">\n" +
    "								<span ng-show=\"{{profileInfo.intervalMs > 999}}\">{{profileInfo.intervalMs | millSecondsToTimeString}}</span>\n" +
    "								<span ng-hide=\"{{profileInfo.intervalMs > 999}}\">{{profileInfo.intervalMs}} ms</span>\n" +
    "							</div>\n" +
    "						</div>\n" +
    "						<div class=\"row\">\n" +
    "							<div class=\"col-sm-5 text-right\">Total Captured:</div>\n" +
    "							<div class=\"col-sm-7\">{{profileInfo.snapshotCount}}</div>\n" +
    "						</div>\n" +
    "					</div> <!-- container -->\n" +
    "				</div> <!-- panel body -->\n" +
    "			</div> <!-- panel -->\n" +
    "\n" +
    "			<div class=\"panel panel-default\">\n" +
    "				<div class=\"panel-heading\">Recorded Traces</div>\n" +
    "				<div class=\"panel-body scrollHorizontal\">\n" +
    "					<div ng-repeat=\"profile in profiles\">\n" +
    "						<a ng-click=\"showTrace(profile)\">{{profile.count}}:{{profile.description}}</a>\n" +
    "					</div>\n" +
    "					<div ng-hide=\"profiles\">None Recorded</div>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "		</div>\n" +
    "\n" +
    "		<div class=\"col-md-7\">\n" +
    "			<div class=\"panel panel-default\">\n" +
    "				<div class=\"panel-heading\">Trace Details</div>\n" +
    "				<div class=\"panel-body scrollHorizontal\" ng-show=\"stacktrace\">\n" +
    "					<p><strong>{{stacktrace.description}}</strong><br/>Seen {{stacktrace.count}} times</p>\n" +
    "					<div ng-repeat=\"analysis in stacktrace.analysis\" style=\"margin-top:2em;\">\n" +
    "						<div><a href=\"#\" onClick=\"return false;\" ng-click=\"showtrace(stacktrace)\">Seen {{analysis.count}} times: {{analysis.description}}</a></div>\n" +
    "						<div class=\"seethread\" ng-repeat=\"thread in analysis.threads\">\n" +
    "							<div>\n" +
    "								<abbr ng-show=\"thread.ref\" class=\"threadName {{thread.importance}}\" title=\"{{infos[thread.ref].description}}\">Example {{$index+1}} (Seen {{thread.count}} times):</abbr>\n" +
    "								<span ng-hide=\"thread.ref\" class=\"threadName {{thread.importance}}\">{{thread.name}}</span>\n" +
    "							</div>\n" +
    "							<div ng-repeat=\"method in thread.methods\" class=\"method\">\n" +
    "								<span ng-show=\"method.package\">\n" +
    "									<abbr class=\"{{seestack.infos[method.packageInfo].importance}}\" title=\"{{infos[method.packageInfo].description}}\">{{method.package}}</abbr>\n" +
    "									<abbr ng-show=\"method.methodInfo\" class=\"{{method.importance}}\" title=\"{{infos[method.methodInfo].description}}\">{{method.rawLineAfterPackage}}</abbr>\n" +
    "									<span ng-hide=\"method.methodInfo\" class=\"{{method.importance}}\">{{method.rawLineAfterPackage}}</span>\n" +
    "								</span>\n" +
    "								<span ng-hide=\"method.package\" class=\"{{method.importance}}\">\n" +
    "									<abbr ng-show=\"method.methodInfo\" class=\"{{method.importance}}\" title=\"{{infos[method.methodInfo].description}}\">{{method.rawLine}}</abbr>\n" +
    "									<span ng-hide=\"method.methodInfo\" class=\"{{method.importance}}\">{{method.rawLine}}</span>\n" +
    "								</span>\n" +
    "							</div>\n" +
    "						</div>\n" +
    "					</div>\n" +
    "				</div>\n" +
    "				<div class=\"panel-body\" ng-hide=\"stacktrace\">\n" +
    "					None Recorded\n" +
    "				</div>\n" +
    "			</div>\n" +
    "		</div>\n" +
    "	</div>\n" +
    "</div>");
}]);

angular.module("profiling/profiling.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("profiling/profiling.tpl.html",
    "<div ng-show=\"initialLoading\" class=\"alert relative alert-info\"><img src=\"/img/spinner.gif\"> Loading Profile Data...</div>\n" +
    "<div class=\"container-fluid\" ng-hide=\"initialLoading\">\n" +
    "	<div class=\"row\">\n" +
    "		<div class=\"col-md-5\">\n" +
    "			\n" +
    "			<form class=\"form-horizontal\" name=\"newProfileForm\" novalidate> \n" +
    "\n" +
    "			<div class=\"col-md-11 nopadding panel panel-default\">\n" +
    "				<div class=\"panel-heading\">\n" +
    "					<h3 class=\"panel-title\">\n" +
    "						<span ng-hide=\"active.id\">Record a New Profile:</span>\n" +
    "						<span ng-show=\"active.id\"><img src=\"/img/spinner_bar.gif\" alt=\"Recording\"> Recording...</span>\n" +
    "					</h3>\n" +
    "				</div>\n" +
    "\n" +
    "				<div class=\"content panel-body\" id=\"active-profiler-status\">\n" +
    "\n" +
    "					<div ng-hide=\"active.id\"> \n" +
    "					<!-- profile start form, hidden when profile is recording -->\n" +
    "\n" +
    "						<div class=\"form-group\" ng-class=\"{'has-error' : newProfileForm.name.$dirty && newProfileForm.name.$invalid}\">\n" +
    "							<label class=\"col-sm-2 control-label\" for=\"inputName\">Label</label>\n" +
    "							<div class=\"col-sm-8\">\n" +
    "								<input type=\"text\" id=\"name\" name=\"name\" placeholder=\"My Profile\" ng-model=\"newProfile.name\" ng-required ng-pattern=\"/^[a-zA-Z0-9 ]*$/\" class=\"form-control\" />\n" +
    "								<div role=\"alert\" class=\"errorHighlight hint\" ng-show=\"newProfileForm.name.$dirty\">\n" +
    "					      		<span  ng-show=\"newProfileForm.name.$error.required\">Please enter a label for this profile</span>\n" +
    "					      		<span  ng-show=\"newProfileForm.name.$error.pattern\">Labels can only contain letters, numbers and spaces</span>\n" +
    "					    		</div>\n" +
    "							</div>\n" +
    "						</div>\n" +
    "\n" +
    "						<div class=\"form-group\"  ng-class=\"{'has-error' : newProfileForm.scheduledDurationMin.$invalid && startClicked}\">\n" +
    "							<label class=\"col-sm-2 control-label\" for=\"scheduledDurationMin\">Duration</label>\n" +
    "							<div class=\"col-sm-4\">\n" +
    "								<select id=\"scheduledDurationMin\" name=\"scheduledDurationMin\" ng-model=\"newProfile.scheduledDurationMin\" class=\"input-mini form-control\" required  ng-options=\"d as (d*60000 | durationString) for d in durationOptions\" ng-disabled=\"!newProfile.name\" ng-change=\"getIntervalOptions(newProfile.scheduledDurationMin)\"></select>\n" +
    "\n" +
    "								<div role=\"alert\" class=\"errorHighlight hint\" ng-show=\"startClicked\">\n" +
    "					      			<span ng-show=\"newProfileForm.scheduledDurationMin.$invalid\">Choose a duration</span>\n" +
    "					    		</div>\n" +
    "							</div>\n" +
    "							<div class=\"col-sm-4 hint\">\n" +
    "								How long the profile will record \n" +
    "							</div>\n" +
    "						</div>\n" +
    "\n" +
    "						<div class=\"form-group\" ng-class=\"{'has-error' : newProfileForm.interval.$invalid  && startClicked}\">\n" +
    "							<label class=\"col-sm-2 control-label\" for=\"interval\">Interval</label>\n" +
    "							<div class=\"col-sm-4\">\n" +
    "								<select id=\"interval\" name=\"interval\" ng-model=\"newProfile.interval\" class=\"input-mini form-control\" required  ng-options=\"i as (i | durationString) for i in durationIntervalOptions\" ng-disabled=\"!newProfile.scheduledDurationMin || !durationIntervalOptions.length\"></select>\n" +
    "\n" +
    "								<div role=\"alert\" class=\"errorHighlight hint\" ng-show=\"startClicked\">\n" +
    "					      			<span ng-show=\"newProfileForm.interval.$invalid\">Choose an Interval</span>\n" +
    "					    		</div>\n" +
    "							</div>\n" +
    "							<div class=\"col-sm-4 hint\">\n" +
    "								How often a snapshot is recorded\n" +
    "							</div>\n" +
    "						</div>\n" +
    "\n" +
    "						<div class=\"form-group\">\n" +
    "							<label class=\"col-sm-2 control-label\"> </label>\n" +
    "							<div class=\"col-sm-8\">\n" +
    "								<button class=\"btn btn-primary\" href=\"#\" onClick=\"return false;\" ng-click=\"startProfiling()\" ng-hide=\"starting\" ng-disabled=\"newProfileForm.$invalid\">Start Profiler</button> \n" +
    "								<button class=\"btn btn-warning\" href=\"#\" ng-show=\"starting\"><img src=\"/img/spinner_circle.gif\" alt=\"Starting\"> Starting</button> \n" +
    "							</div>\n" +
    "						</div>\n" +
    "					</div> <!-- profile form div -->\n" +
    "		\n" +
    "					<div ng-show=\"active.id\">\n" +
    "						\n" +
    "						<h4>{{active.name}}</h4>\n" +
    "						<div class=\"em\">{{active.message}}</div>\n" +
    "						<progressbar value=\"timePercent\" title=\"Time Remaining\" animated=\"true\"></progressbar>\n" +
    "\n" +
    "						<div class=\"col-sm-6 text-center\">\n" +
    "							<div class=\"panel panel-default\">\n" +
    "								<div class=\"panel-heading\"><b>Time Left</b></div>\n" +
    "								<div class=\"panel-body\"><h3 class=\"nopadding\">{{activeTimeRemaining  | millSecondsToTimerString}}</h3></div>\n" +
    "							</div>\n" +
    "						</div>\n" +
    "						<div class=\"col-sm-6 text-center\">\n" +
    "							<div class=\"panel panel-default\">\n" +
    "								<div class=\"panel-heading\"><b>Recorded</b></div>\n" +
    "								<div class=\"panel-body\"><h3 class=\"nopadding\">{{active.snapshotCount}}</h3></div>\n" +
    "							</div>\n" +
    "						</div>\n" +
    "\n" +
    "						<table border=\"0\" class=\"padded\">\n" +
    "						<tr>\n" +
    "							<td class=\"listLabel\">Recording Started: </td>\n" +
    "							<td>{{active.startTick | date: 'MMM dd, yyyy @ h:mm a'}}</td>\n" +
    "						</tr>\n" +
    "						<tr>\n" +
    "							<td class=\"listLabel\">Recording Interval: </td>\n" +
    "							<td>{{active.intervalMs  | millSecondsToReadable}}</td>\n" +
    "						</tr>\n" +
    "						<tr>\n" +
    "							<td class=\"listLabel\">Scheduled Duration:  </td>\n" +
    "							<td>{{active.scheduledDurationMs | millSecondsToTimeString}}</td>\n" +
    "						</tr>\n" +
    "						</table>\n" +
    "						<p></p>\n" +
    "						<button class=\"btn btn-danger\" href=\"#\" onClick=\"return false;\" ng-click=\"stopProfiling()\" ng-hide=\"stopping\">Stop Profiler</button>\n" +
    "						<button class=\"btn btn-warning\" href=\"#\" ng-show=\"stopping\"><img src=\"/img/spinner_circle.gif\" alt=\"Stopping\"> Stopping</button> \n" +
    "							\n" +
    "					</div> <!-- profile recording div -->\n" +
    "				</div> <!-- left panel body -->\n" +
    "			</div> <!-- left panel -->\n" +
    "			</form>\n" +
    "		</div>\n" +
    "		\n" +
    "		<div class=\"col-md-7\" ng-show=\"status['saved']\">\n" +
    "			\n" +
    "				<div class=\"row\">\n" +
    "					<div class=\"col-sm-6\"><h2 class=\"subtitle\">Saved Profiles</h2></div>\n" +
    "					<div class=\"col-sm-6 text-right\"><label>Search: <input ng-model=\"searchText\"></label></div>\n" +
    "				</div>\n" +
    "			\n" +
    "				<table class=\"table table-striped table-hover\">\n" +
    "					<tr>\n" +
    "						<th>Label</th>\n" +
    "						<th>Record Date</th>\n" +
    "						<th>Duration</th>\n" +
    "						<th>Snapshots</th>\n" +
    "						<th></th>\n" +
    "						<th></th>\n" +
    "					</tr>\n" +
    "					<tr ng-repeat=\"profile in filtered = status['saved'] | filter:searchText | startFrom:(currentPage-1)*pageSize | limitTo:pageSize\">\n" +
    "						<td>{{profile.name}}</td>\n" +
    "						<td>{{profile.startTick | date: 'MMM dd yyyy h:mm a'}}</td>\n" +
    "						<td>{{profile.actualDurationMs | millSecondsToTimeString}}</td>\n" +
    "						<td>{{profile.snapshotCount}}</td>\n" +
    "						<td>\n" +
    "							<a ui-sref=\"profile({id:profile.id})\"><span class=\"glyphbutton glyphicons list\" title=\"Review this Profile\"></span></a>\n" +
    "						</td>\n" +
    "						<td>\n" +
    "							<span class=\"glyphbutton glyphicons remove linkish\" ng-click=\"removeProfile(profile)\" title=\"Delete this Profile\"></span>\n" +
    "						</td>\n" +
    "					</tr>\n" +
    "					<tr ng-show=\"profiles.length==0\">\n" +
    "						<td colspan=\"6\"><div class=\"alert relative alert-info\" role=\"alert\">No Saved Profiles.</div></td>\n" +
    "					</tr>\n" +
    "				</table>\n" +
    "\n" +
    "				<pagination ng-model=\"currentPage\" total-items=\"totalItems\" items-per-page=\"pageSize\" class=\"pagination-sm\" ng-show=\"noOfPages > 1\"></pagination>\n" +
    "\n" +
    "				<!--  <pagination page=\"currentPage\" max-size=\"noOfPages\" total-items=\"totalItems\" items-per-page=\"pageSize\"></pagination> -->\n" +
    "		</div>\n" +
    "	</div>\n" +
    "</div>");
}]);

angular.module("server/active.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("server/active.tpl.html",
    "<div class=\"requestList\" ng-hide=\"lookingRequest.on\">\n" +
    "	<table class=\"table table-striped table-hover table-condensed results\">\n" +
    "		<thead>\n" +
    "			<tr>\n" +
    "				<th ng-click=\"sortType = 'url'; sortReverse = !sortReverse\">\n" +
    "		            Request \n" +
    "		            <span ng-show=\"sortType == 'url' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'url' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<!-- <th ng-click=\"sortType = 'completed'; sortReverse = !sortReverse\">\n" +
    "		            Completed \n" +
    "		            <span ng-show=\"sortType == 'completed' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'completed' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th> -->\n" +
    "				<th ng-click=\"sortType = 'ip'; sortReverse = !sortReverse\">\n" +
    "		            From IP\n" +
    "		            <span ng-show=\"sortType == 'ip' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'ip' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'responseCode'; sortReverse = !sortReverse\">\n" +
    "		            Response\n" +
    "		            <span ng-show=\"sortType == 'responseCode' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'responseCode' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'time'; sortReverse = !sortReverse\">\n" +
    "		            Elapsed\n" +
    "		            <span ng-show=\"sortType == 'time' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'time' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'queryCount'; sortReverse = !sortReverse\">\n" +
    "		            Queries\n" +
    "		            <span ng-show=\"sortType == 'queryCount' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'queryCount' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'queryTime'; sortReverse = !sortReverse\">\n" +
    "		            QryTime\n" +
    "		            <span ng-show=\"sortType == 'queryTime' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'queryTime' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'longQueryElapsed'; sortReverse = !sortReverse\">\n" +
    "		            LongQry\n" +
    "		            <span ng-show=\"sortType == 'longQueryElapsed' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'longQueryElapsed' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'longQueryRows'; sortReverse = !sortReverse\">\n" +
    "		            Rows\n" +
    "		            <span ng-show=\"sortType == 'longQueryRows' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'longQueryRows' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'longQuerySql'; sortReverse = !sortReverse\">\n" +
    "		            SQL\n" +
    "		            <span ng-show=\"sortType == 'longQuerySql' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'longQuerySql' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "			</tr>\n" +
    "		</thead>\n" +
    "		<tbody>\n" +
    "			<tr ng-show=\"pages.length==0 && !busy\">\n" +
    "				<td colspan=\"10\">No active requests</td>\n" +
    "			</tr>\n" +
    "			<tr ng-repeat=\"page in pages | orderBy:sortType:sortReverse\" ng-class=\"{highlighted:page.pid === detailRequestNumber}\">\n" +
    "				<td>\n" +
    "					<!-- <button ng-click=\"getStack(page)\" type=\"button\" href=\"#\" onClick=\"return false;\"><img src=\"images/stack.gif\" border=0></button> -->\n" +
    "					<!-- <button ng-click=\"kill(page)\" ng-show=\"canKill\" type=\"button\" href=\"#\" onClick=\"return false;\"><img src=\"images/stop.png\" border=0></button> -->\n" +
    "					<button ng-click=\"getStack(page)\" title=\"View Stack\"><span class=\"glyphicons show_lines blue\"></span></button>\n" +
    "					<button ng-click=\"kill(page)\" ng-if=\"canKill\"  title=\"Kill Request\"><span class=\"glyphicons stop red\"></span></button>\n" +
    "					<button ng-click=\"open(page)\" ng-if=\"clickableURLs\" title=\"Open this URL\"><span class=\"glyphicons share green\"></span></button>\n" +
    "					<span ng-click=\"requestDetails(page)\">{{page.url | limitTo : 70}}</span><span ng-show=\"page.url.length > 70\">...</span>\n" +
    "				</td>\n" +
    "				<!-- <td ng-click=\"requestDetails(page)\">{{page.completed | date:'short'}}</td> -->\n" +
    "				<td nowrap>\n" +
    "					<span ng-show=\"dosEnabled\" class=\"glyphicons ban\" ng-click=\"addBlock(page.ip)\" title=\"Block {{page.ip}}\"></span>\n" +
    "					<span ng-show=\"clickableIPURL\" class=\"glyphicons globe\" ng-click=\"open(clickableIPURL + page.ip)\" title=\"IP info\"></span>\n" +
    "					{{page.ip}}\n" +
    "				</td>\n" +
    "				<td ng-click=\"requestDetails(page)\">{{page.responseCode}}<span ng-show=\"page.timeToFirstByte > 0\">@{{page.timeToFirstByte}}ms</span></td>\n" +
    "				<td ng-click=\"requestDetails(page)\">{{page.time}}ms</td>\n" +
    "				<td ng-click=\"requestDetails(page)\">{{page.queryCount}}</td>\n" +
    "				<td ng-click=\"requestDetails(page)\">{{page.queryTime}}ms</td>\n" +
    "				<td ng-click=\"requestDetails(page)\">{{page.longQueryElapsed}}ms</td>\n" +
    "				<td ng-click=\"requestDetails(page)\">{{page.longQueryRows}}</td>\n" +
    "				<td><span ng-click=\"requestDetails(page)\">{{page.longQuerySql | limitTo : 70}}</span><span ng-show=\"page.longQuerySql.length > 70\">...</span></td>\n" +
    "			</tr>\n" +
    "		</tbody>\n" +
    "	</table>\n" +
    "</div>\n" +
    "");
}]);

angular.module("server/chart-modal.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("server/chart-modal.tpl.html",
    "<div class=\"modal-header\">\n" +
    "	<h3 class=\"modal-title\">{{charts.title}}</h3>\n" +
    "</div>\n" +
    "<div class=\"modal-body chart\">\n" +
    "	<div ng-if=\"charts.data.length\">\n" +
    "		<nvd3-line-chart\n" +
    "		id=\"chartModal\"\n" +
    "		data=\"charts.data\"\n" +
    "		showXAxis=\"true\"\n" +
    "		showYAxis=\"true\"\n" +
    "		tooltips=\"true\"\n" +
    "		interactive=\"true\"\n" +
    "		showLegend=\"true\"\n" +
    "		xAxisTickFormat=\"chartDateFormat()\"\n" +
    "		height=\"{{charts.height}}\">\n" +
    "		</nvd3-line-chart>\n" +
    "	</div>\n" +
    "</div>\n" +
    "\n" +
    "<div class=\"modal-footer\">\n" +
    "	<button class=\"btn btn-default\" ng-click=\"cancel()\">Close</button>\n" +
    "</div>\n" +
    "");
}]);

angular.module("server/chart-modal2.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("server/chart-modal2.tpl.html",
    "<div class=\"modal-header\">\n" +
    "	<h3 class=\"modal-title\">{{modal.title}}</h3>\n" +
    "</div>\n" +
    "	<div class=\"modal-body chart\">\n" +
    "		<nvd3-line-chart\n" +
    "		id=\"chartModal\"\n" +
    "		data=\"charts[modal.chart]\"\n" +
    "		showXAxis=\"true\"\n" +
    "		showYAxis=\"true\"\n" +
    "		tooltips=\"true\"\n" +
    "		interactive=\"true\"\n" +
    "		showLegend=\"true\"\n" +
    "		xAxisTickFormat=\"chartDateFormat()\">\n" +
    "		</nvd3-line-chart>\n" +
    "	</div>\n" +
    "		\n" +
    "</div>\n" +
    "\n" +
    "<div class=\"modal-footer\">\n" +
    "	<button class=\"btn btn-default\" ng-click=\"modalCancel()\">Close</button>\n" +
    "</div>");
}]);

angular.module("server/recent.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("server/recent.tpl.html",
    "<div class=\"requestList\" ng-hide=\"lookingRequest.on\">\n" +
    "	<table class=\"table table-striped table-hover table-condensed results\">\n" +
    "		<thead>\n" +
    "			<tr>\n" +
    "				<th ng-click=\"sortType = 'url'; sortReverse = !sortReverse\">\n" +
    "		            Request\n" +
    "		            <span ng-show=\"sortType == 'url' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'url' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'completed'; sortReverse = !sortReverse\">\n" +
    "		            Completed \n" +
    "		            <span ng-show=\"sortType == 'completed' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'completed' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'ip'; sortReverse = !sortReverse\">\n" +
    "		            From IP\n" +
    "		            <span ng-show=\"sortType == 'ip' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'ip' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'responseCode'; sortReverse = !sortReverse\">\n" +
    "		            Response\n" +
    "		            <span ng-show=\"sortType == 'responseCode' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'responseCode' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'time'; sortReverse = !sortReverse\">\n" +
    "		            Elapsed\n" +
    "		            <span ng-show=\"sortType == 'time' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'time' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'queryCount'; sortReverse = !sortReverse\">\n" +
    "		            Queries\n" +
    "		            <span ng-show=\"sortType == 'queryCount' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'queryCount' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'queryTime'; sortReverse = !sortReverse\">\n" +
    "		            QryTime\n" +
    "		            <span ng-show=\"sortType == 'queryTime' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'queryTime' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'longQueryElapsed'; sortReverse = !sortReverse\">\n" +
    "		            LongQry\n" +
    "		            <span ng-show=\"sortType == 'longQueryElapsed' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'longQueryElapsed' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'longQueryRows'; sortReverse = !sortReverse\">\n" +
    "		            Rows\n" +
    "		            <span ng-show=\"sortType == 'longQueryRows' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'longQueryRows' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'longQuerySql'; sortReverse = !sortReverse\"> \n" +
    "		            SQL\n" +
    "		            <span ng-show=\"sortType == 'longQuerySql' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'longQuerySql' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "			</tr>\n" +
    "		</thead>\n" +
    "		<tbody>\n" +
    "			<tr ng-show=\"pages.length==0 && !busy\"> \n" +
    "				<td colspan=\"10\">No recent requests.</td>\n" +
    "			</tr>\n" +
    "			<!-- <tr><td colspan=8>{{pages[0] | json}}</td></tr> -->\n" +
    "			<tr class=\"requestRow\" ng-repeat=\"page in pages | orderBy:sortType:sortReverse\" ng-class=\"{highlighted:page.pid === detailRequestNumber}\">\n" +
    "				<td><button ng-click=\"open(page)\"  ng-if=\"clickableURLs\" title=\"Open this URL\"><span class=\"glyphicons share green\"></span></button> <span ng-click=\"requestDetails(page)\">{{page.url | limitTo : 70}}</span><span ng-show=\"page.url.length > 70\">...</span></td>\n" +
    "				<td><span ng-show=\"displayRelativeTimes\">{{page.completedAgoMs | millSecondsToTimeString}} ago</span><span ng-show=\"!displayRelativeTimes\">{{page.completed | date:'mediumTime' | lowercase}}</span></td>\n" +
    "				<td nowrap>\n" +
    "					<span ng-show=\"dosEnabled\" class=\"glyphicons ban\" ng-click=\"addBlock(page.ip)\" title=\"Block {{page.ip}}\"></span>\n" +
    "					<span ng-show=\"clickableIPURL\" class=\"glyphicons globe\" ng-click=\"open(clickableIPURL + page.ip)\" title=\"IP info\"></span>\n" +
    "					{{page.ip}}\n" +
    "				</td> \n" +
    "				<td>{{page.responseCode}}<span ng-show=\"page.timeToFirstByte > 0\">@{{page.timeToFirstByte}}ms</span></td>\n" +
    "				<td>{{page.time}}ms</td>\n" +
    "				<td>{{page.queryCount}}</td>\n" +
    "				<td>{{page.queryTime}}ms</td>\n" +
    "				<td>{{page.longQueryElapsed}}ms</td>\n" +
    "				<td>{{page.longQueryRows}}</td>\n" +
    "				<td><span ng-click=\"requestDetails(page)\">{{page.longQuerySql | limitTo : 70}}</span><span ng-show=\"page.longQuerySql.length > 70\">...</span></td>\n" +
    "			</tr> \n" +
    "		</tbody>\n" +
    "	</table>\n" +
    "</div> ");
}]);

angular.module("server/requestDetails.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("server/requestDetails.tpl.html",
    "<div ng-hide=\"lookingRequest.info\" class=\"alert relative alert-info\" role=\"alert\">\n" +
    "  No request details available.\n" +
    "</div>\n" +
    "\n" +
    "<div ng-show=\"lookingRequest.info\" class=\"requestDetail\">\n" +
    "\n" +
    "<table class=\"table table-striped table-hover\">\n" +
    "	<tbody>\n" +
    "		<tr>\n" +
    "			<td class=\"listLabel\">Request:</td>\n" +
    "			<td width=\"90%\"><button ng-click=\"open(lookingRequest.info)\"><span class=\"glyphicons share green\"></span></button> {{lookingRequest.info.url}}</td>\n" +
    "		</tr>\n" +
    "		<tr>\n" +
    "			<td class=\"listLabel\">Thread:</td>\n" +
    "			<td width=\"90%\">{{lookingRequest.info.threadName}} (Req #{{lookingRequest.info.pid}})</td>\n" +
    "		</tr>\n" +
    "		<tr>\n" +
    "			<td class=\"listLabel\">Completed:</td>\n" +
    "			<td width=\"90%\">{{lookingRequest.info.completed | date:'short'}} ({{lookingRequest.info.completedAgoMs | millSecondsToTimeString}} ago)</td>\n" +
    "		</tr>\n" +
    "		<tr>\n" +
    "			<td class=\"listLabel\" >Source IP:</td>\n" +
    "			<td width=\"90%\">{{lookingRequest.info.ip}} \n" +
    "				<span ng-show=\"dosEnabled\" class=\"glyphicons ban\" ng-click=\"addBlock(lookingRequest.info.ip)\" title=\"Block {{page.ip}}\"></span>\n" +
    "				<span ng-show=\"clickableIPURL\" class=\"glyphicons globe\" ng-click=\"open(clickableIPURL + lookingRequest.info.ip)\" title=\"IP info\"></span>\n" +
    "			</td>\n" +
    "		</tr>\n" +
    "		<tr>\n" +
    "			<td class=\"listLabel\" text-right>HTTP Response Code:</td>\n" +
    "			<td width=\"90%\">{{lookingRequest.info.responseCode}} ({{lookingRequest.info.method}}, HTTP<span ng-if=\"lookingRequest.info.isSecure\">S</span>)</td>\n" +
    "		</tr>\n" +
    "		<tr>\n" +
    "			<td class=\"listLabel\">Time to First Byte:</td>\n" +
    "			<td width=\"90%\"><span ng-if=\"lookingRequest.info.timeToFirstByte\">{{lookingRequest.info.timeToFirstByte}} ms</span></td>\n" +
    "		</tr>\n" +
    "		<tr>\n" +
    "			<td class=\"listLabel\">Total Time:</td>\n" +
    "			<td width=\"90%\">{{lookingRequest.info.time}} ms</td>\n" +
    "		</tr>\n" +
    "		<tr ng-show=\"lookingRequest.info.trace\">\n" +
    "			<td class=\"listLabel\">Trace:</td>\n" +
    "			<td width=\"90%\">{{lookingRequest.info.trace}}</td>\n" +
    "		</tr>\n" +
    "		<tr>\n" +
    "			<td class=\"listLabel\">Completed Queries:</td>\n" +
    "			<td width=\"90%\">\n" +
    "				<span ng-show=\"lookingRequest.info.queryCount\">{{lookingRequest.info.queryCount}} ({{lookingRequest.info.queryTime}} ms)</span>\n" +
    "				<span ng-hide=\"lookingRequest.info.queryCount\">No Queries Logged</span>\n" +
    "			</td>\n" +
    "		</tr>\n" +
    "		<tr ng-show=\"lookingRequest.info.queryCount\">\n" +
    "			<td class=\"listLabel\">Long Query:</td>\n" +
    "			<td width=\"90%\">\n" +
    "				<p ng-show=\"lookingRequest.info.longQueryActive\">STILL ACTIVE</p>\n" +
    "				{{lookingRequest.info.longQueryElapsed}} ms,  {{lookingRequest.info.longQueryRows}} rows\n" +
    "				<span ng-show=\"lookingRequest.info.longQueryActive\">(so far)</span>\n" +
    "				<span ng-show=\"lookingRequest.info.longQuerySql\">:</span>\n" +
    "				<div ng-show=\"lookingRequest.info.longQuerySql\">\n" +
    "					<pre>{{lookingRequest.info.longQuerySql}}</pre>\n" +
    "				</div>\n" +
    "			</td>\n" +
    "		</tr>\n" +
    "	</tbody>\n" +
    "</table>\n" +
    "");
}]);

angular.module("server/server.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("server/server.tpl.html",
    "<div class=\"content\">\n" +
    "	<div>\n" +
    "		<ul class=\"nav nav-tabs\">\n" +
    "		  <li ng-class=\"{active:$state.includes('server.active') && !lookingRequest.on}\"><a ui-sref=\"server.active\" ng-click=\"closeDetail()\">Active Requests</a></li>\n" +
    "		  <li ng-class=\"{active:$state.includes('server.recent') && !lookingRequest.on}\"><a ui-sref=\"server.recent\" ng-click=\"closeDetail()\">Recent Requests</a></li>\n" +
    "		  <li ng-class=\"{active:$state.includes('server.slow') && !lookingRequest.on}\"><a ui-sref=\"server.slow\" ng-click=\"closeDetail()\">Slow Requests</a></li>\n" +
    "		  <li ng-show=\"lookingRequest.on\" ng-class=\"{active:lookingRequest.on}\"><a name=\"details\">Request Details <span class=\"glyphicons remove leftMargin\" ng-click=\"closeDetail();\"></span></a></li>\n" +
    "		</ul>\n" +
    "		<!-- <div ng-show=\"ititialLoading\" class=\"alert relative alert-info\"><img src=\"/img/spinner.gif\"> Loading Request Data...</div> -->\n" +
    "		<div class=\"push-down\" ui-view></div> \n" +
    "	</div>\n" +
    "</div>\n" +
    "\n" +
    "<div class=\"push-down\" ng-hide=\"lookingRequest.on\">\n" +
    "\n" +
    "	<div id=\"pagesChart\" class=\"col-md-4\">\n" +
    "		<div class=\"panel panel-default\">\n" +
    "			<div class=\"panel-heading\" ng-click=\"expandChart('pages','Pages')\">\n" +
    "				<div class=\"pull-right glyphicons resize_full\"></div>\n" +
    "				<h3 class=\"panel-title\">Pages</h3>\n" +
    "			</div>\n" +
    "			<div class=\"panel-body chart\">\n" +
    "				<div ng-if=\"charts.pages.length\">\n" +
    "					<nvd3-line-chart\n" +
    "					id=\"chartPages\"\n" +
    "					data=\"charts.pages\"\n" +
    "					showXAxis=\"true\"\n" +
    "					showYAxis=\"true\"\n" +
    "					tooltips=\"true\"\n" +
    "					interactive=\"true\"\n" +
    "					showLegend=\"true\"\n" +
    "					xAxisTickFormat=\"chartDateFormat()\">\n" +
    "					</nvd3-line-chart>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "		</div>\n" +
    "	</div>\n" +
    "	<div id=\"queriesChart\" class=\"col-md-4\">\n" +
    "		<div class=\"panel panel-default\">\n" +
    "			<div class=\"panel-heading\" ng-click=\"expandChart('queries','Queries')\">\n" +
    "				<div class=\"pull-right glyphicons resize_full\"></div>\n" +
    "				<h3 class=\"panel-title\">Queries</h3>\n" +
    "			</div>\n" +
    "			<div class=\"panel-body chart\">\n" +
    "				<div ng-if=\"charts.queries.length\">\n" +
    "					<nvd3-line-chart\n" +
    "					id=\"chartQueries\"\n" +
    "					data=\"charts.queries\"\n" +
    "					showXAxis=\"true\"\n" +
    "					showYAxis=\"true\"\n" +
    "					tooltips=\"true\"\n" +
    "					interactive=\"true\"\n" +
    "					showLegend=\"true\"\n" +
    "					xAxisTickFormat=\"chartDateFormat()\">\n" +
    "					</nvd3-line-chart>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "		</div>\n" +
    "	</div>\n" +
    "	<div id=\"memoryChart\" class=\"col-md-4\">\n" +
    "		<div class=\"panel panel-default\">\n" +
    "			<div class=\"panel-heading\" ng-click=\"expandChart('memory','Memory')\">\n" +
    "				<div class=\"pull-right glyphicons resize_full\"></div>\n" +
    "				<h3 class=\"panel-title\">Memory</h3>\n" +
    "			</div>\n" +
    "			<div class=\"panel-body chart\">\n" +
    "				<div ng-if=\"charts.memory.length\">\n" +
    "					<nvd3-line-chart\n" +
    "					id=\"chartMemory\"\n" +
    "					data=\"charts.memory\"\n" +
    "					showXAxis=\"true\"\n" +
    "					showYAxis=\"true\"\n" +
    "					tooltips=\"true\"\n" +
    "					interactive=\"true\"\n" +
    "					showLegend=\"true\"\n" +
    "					xAxisTickFormat=\"chartDateFormat()\">\n" +
    "					</nvd3-line-chart>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "		</div>\n" +
    "	</div>\n" +
    "</div>\n" +
    "\n" +
    "<div class=\"push-down\" ng-show=\"lookingRequest.on\" ng-include=\"'server/requestDetails.tpl.html'\"></div>\n" +
    "");
}]);

angular.module("server/slow.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("server/slow.tpl.html",
    "<div class=\"requestList\" ng-hide=\"lookingRequest.on\">\n" +
    "	<table class=\"table table-striped table-hover table-condensed results\">\n" +
    "		<thead>\n" +
    "			<tr>\n" +
    "				<th ng-click=\"sortType = 'url'; sortReverse = !sortReverse\">\n" +
    "		            Request \n" +
    "		            <span ng-show=\"sortType == 'url' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'url' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'completed'; sortReverse = !sortReverse\">\n" +
    "		            Completed \n" +
    "		            <span ng-show=\"sortType == 'completed' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'completed' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'ip'; sortReverse = !sortReverse\">\n" +
    "		            From IP\n" +
    "		            <span ng-show=\"sortType == 'ip' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'ip' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'responseCode'; sortReverse = !sortReverse\">\n" +
    "		            Response\n" +
    "		            <span ng-show=\"sortType == 'responseCode' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'responseCode' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'time'; sortReverse = !sortReverse\">\n" +
    "		            Elapsed\n" +
    "		            <span ng-show=\"sortType == 'time' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'time' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'queryCount'; sortReverse = !sortReverse\">\n" +
    "		            Queries\n" +
    "		            <span ng-show=\"sortType == 'queryCount' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'queryCount' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'queryTime'; sortReverse = !sortReverse\">\n" +
    "		            QryTime\n" +
    "		            <span ng-show=\"sortType == 'queryTime' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'queryTime' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'longQueryElapsed'; sortReverse = !sortReverse\">\n" +
    "		            LongQry\n" +
    "		            <span ng-show=\"sortType == 'longQueryElapsed' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'longQueryElapsed' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'longQueryRows'; sortReverse = !sortReverse\">\n" +
    "		            Rows\n" +
    "		            <span ng-show=\"sortType == 'longQueryRows' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'longQueryRows' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "				<th ng-click=\"sortType = 'longQuerySql'; sortReverse = !sortReverse\">\n" +
    "		            SQL\n" +
    "		            <span ng-show=\"sortType == 'longQuerySql' && !sortReverse\" class=\"caret\"></span>\n" +
    "		            <span ng-show=\"sortType == 'longQuerySql' && sortReverse\" class=\"dropup\"><span class=\"caret\"></span></span>\n" +
    "				</th>\n" +
    "			</tr>\n" +
    "		</thead>\n" +
    "		<tbody>\n" +
    "			<tr ng-show=\"pages.length==0 && !busy\">\n" +
    "				<td colspan=\"10\">No slow requests.</td>\n" +
    "			</tr>\n" +
    "			<tr ng-repeat=\"page in pages | orderBy:sortType:sortReverse\"  ng-class=\"{highlighted:page.requestNumber === detailRequestNumber}\" id=\"req{{page.requestNumber}}\">\n" +
    "				<td><button ng-click=\"open(page)\" ng-if=\"clickableURLs\" title=\"Open this URL\"><span class=\"glyphicons share green\"></span></button> <span ng-click=\"requestDetails(page)\">{{page.url | limitTo : 70}}</span><span ng-show=\"page.url.length > 70\">...</span></td>\n" +
    "				<td nowrap><span ng-show=\"displayRelativeTimes\">{{page.completedAgoMs | millSecondsToTimeString}} ago</span><span ng-show=\"!displayRelativeTimes\">{{page.completed | date:'short'}}</span></td>\n" +
    "				<td nowrap>\n" +
    "					<span ng-show=\"dosEnabled\" class=\"glyphicons ban\" ng-click=\"addBlock(page.ip)\" title=\"Block {{page.ip}}\"></span>\n" +
    "					<span ng-show=\"clickableIPURL\" class=\"glyphicons globe\" ng-click=\"open(clickableIPURL + page.ip)\" title=\"IP info\"></span>\n" +
    "					{{page.ip}}\n" +
    "				</td>\n" +
    "				<td>{{page.responseCode}}<span ng-show=\"page.timeToFirstByte > 0\">@{{page.timeToFirstByte}}ms</span></td>\n" +
    "				<td>{{page.time}}ms</td>\n" +
    "				<td>{{page.queryCount}}</td>\n" +
    "				<td>{{page.queryTime}}ms</td>\n" +
    "				<td>{{page.longQueryElapsed}}ms</td>\n" +
    "				<td>{{page.longQueryRows}}</td>\n" +
    "				<td><span ng-click=\"requestDetails(page)\">{{page.longQuerySql | limitTo : 70}}</span><span ng-show=\"page.longQuerySql.length > 70\">...</span></td>\n" +
    "			</tr>\n" +
    "		</tbody>\n" +
    "	</table>\n" +
    "</div>");
}]);

angular.module("sidebar.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("sidebar.tpl.html",
    "<ul ng-show=\"showNav\"  class=\"nav nav-pills nav-stacked\" ng-if=\"isAuthenticated\">\n" +
    "	<!-- ng-if=\"isAuthenticated\" -->\n" +
    "	<!-- Server -->\n" +
    "	<li ui-sref=\"server.active\" title=\"Server\"><span class=\"glyphicons imac\" ng-class=\"$state.includes('server') ? 'icon_link' : 'white'\"></span></li>\n" +
    "	<!-- Dashboard -->\n" +
    "	<li ui-sref=\"dashboard.all\" title=\"Dashboard\"><span class=\"glyphicons dashboard\" ng-class=\"$state.includes('dashboard') ? 'icon_link' : 'white'\"></span></li>\n" +
    "	<!-- Counters -->\n" +
    "	<li ui-sref=\"counters\" title=\"Counters\"><span class=\"glyphicons list\" ng-class=\"$state.includes('counters') ? 'icon_link' : 'white'\"></span></li>\n" +
    "	<!-- Configuration -->\n" +
    "	<li ui-sref=\"config\" title=\"Configuration\"><span class=\"glyphicons settings\" ng-class=\"$state.includes('config') ? 'icon_link' : 'white'\"></span></li>\n" +
    "	<!-- Active Monitoring -->\n" +
    "	<li ui-sref=\"monitoring.rules\" title=\"Active Monitoring\"><span class=\"glyphicons cardio\" ng-class=\"$state.includes('monitoring') ? 'icon_link' : 'white'\"></span></li>\n" +
    "	<!-- Stack -->\n" +
    "	<li ui-sref=\"stack.seestack({threadname:'',requestid:''})\" title=\"Stack\"><span class=\"glyphicons show_lines\" ng-class=\"$state.includes('stack') ? 'icon_link' : 'white'\"></span></li>\n" +
    "	<!-- Profiling -->\n" +
    "	<li ui-sref=\"profiling\" title=\"Profiling\"><span class=\"glyphicons stats\" ng-class=\"$state.includes('profiling') || $state.includes('profile') ? 'icon_link' : 'white'\"></span></li>\n" +
    "	<!-- DOS Protection -->\n" +
    "	<li ui-sref=\"dos\" title=\"DOS Protection\"><span class=\"glyphicons shield\" ng-class=\"$state.includes('dos') ? 'icon_link' : 'white'\"></span></li>\n" +
    "	<!-- Logging -->\n" +
    "	<li ui-sref=\"log\" title=\"Log\"><span class=\"glyphicons log_book\" ng-class=\"$state.includes('log') ? 'icon_link' : 'white'\"></span></li>\n" +
    "	<!-- About -->\n" +
    "	<li ui-sref=\"about\" title=\"About\"><span class=\"glyphicons circle_question_mark\"  ng-class=\"$state.includes('about') ? 'icon_link' : 'white'\"></span></li>\n" +
    "	\n" +
    "	<li ng-click=\"logout()\" title=\"Logout\"><span class=\"glyphicons white log_out\"></span></li>\n" +
    "	<!-- <li ng-click=\"toggleNav()\" class=\"dark\"><span class=\"glyphicons white resize_small\"></span></li> -->\n" +
    "</ul> ");
}]);

angular.module("snapshot/seestack.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("snapshot/seestack.tpl.html",
    "<div class=\"seethread\" ng-repeat=\"thread in seestack.threads\">\n" +
    "	<div>\n" +
    "		<abbr ng-show=\"thread.ref\" class=\"threadName {{thread.importance}}\" title=\"{{seestack.infos[thread.ref].description}}\">Thread \"{{thread.name}}\"</abbr>\n" +
    "		<span ng-hide=\"thread.ref\" class=\"threadName {{thread.importance}}\">Thread \"{{thread.name}}\"</span>\n" +
    "	</div>\n" +
    "	<div ng-repeat=\"method in thread.methods\" class=\"method\">\n" +
    "		<span ng-show=\"method.package\">\n" +
    "			<abbr class=\"{{seestack.infos[method.packageInfo].importance}}\" title=\"{{seestack.infos[method.packageInfo].description}}\">{{method.package}}</abbr>\n" +
    "			<abbr ng-show=\"method.methodInfo\" class=\"{{method.importance}}\" title=\"{{seestack.infos[method.methodInfo].description}}\">{{method.rawLineAfterPackage}}</abbr>\n" +
    "			<span ng-hide=\"method.methodInfo\" class=\"{{method.importance}}\">{{method.rawLineAfterPackage}}</span>\n" +
    "		</span>\n" +
    "		<span ng-hide=\"method.package\" class=\"{{method.importance}}\">\n" +
    "			<abbr ng-show=\"method.methodInfo\" class=\"{{method.importance}}\" title=\"{{seestack.infos[method.methodInfo].description}}\">{{method.rawLine}}</abbr>\n" +
    "			<span ng-hide=\"method.methodInfo\" class=\"{{method.importance}}\">{{method.rawLine}}</span>\n" +
    "		</span>\n" +
    "	</div>\n" +
    "</div>\n" +
    "");
}]);

angular.module("snapshot/snapshot.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("snapshot/snapshot.tpl.html",
    "<div class=\"panel\">\n" +
    "	<h3>Snapshot: {{snapshot.timestamp | date: 'medium'}}</h3>\n" +
    "	\n" +
    "	\n" +
    "	<table class=\"table table-striped table-hover table-condensed\">\n" +
    "		<thead>\n" +
    "			<tr>\n" +
    "				<th>Request</th>\n" +
    "				<th>Thread</th>\n" +
    "				<th>Completed</th>\n" +
    "				<th>From</th>\n" +
    "				<th>Response</th>\n" +
    "				<th>Elapsed</th>\n" +
    "				<th>Queries</th>\n" +
    "				<th>QryTime</th>\n" +
    "				<th>LongQry</th>\n" +
    "				<th>Rows</th>\n" +
    "				<th>SQL</th>\n" +
    "			</tr>\n" +
    "		</thead>\n" +
    "		<tbody>\n" +
    "			<tr ng-show=\"snapshot.requests.length==0\">\n" +
    "				<td colspan=\"10\">No active requests.</td>\n" +
    "			</tr>\n" +
    "			<tr ng-repeat=\"page in snapshot.requests\">\n" +
    "				<td>{{page.url}}</td>\n" +
    "				<td>{{page.threadName}}</td>\n" +
    "				<td>{{page.completedFormatted}}</td>\n" +
    "				<td>{{page.ip}}</td>\n" +
    "				<td>{{page.responseCode}}<span ng-show=\"page.timeToFirstByte > 0\">@{{page.timeToFirstByte}}ms</span></td>\n" +
    "				<td>{{page.time}}ms</td>\n" +
    "				<td>{{page.queryCount}}</td>\n" +
    "				<td>{{page.queryTime}}</td>\n" +
    "				<td>{{page.longQueryElapsed}}ms</td>\n" +
    "				<td>{{page.longQueryRows}}</td>\n" +
    "				<td>{{page.longQuerySql}}</td>\n" +
    "			</tr>\n" +
    "		</tbody>\n" +
    "	</table>\n" +
    "	\n" +
    "	\n" +
    "    <ul class=\"nav nav-tabs nav-inner push-down\">\n" +
    "	    <li ng-class=\"{active:$state.includes('snapshot.seestack')}\"><a ui-sref=\"snapshot.seestack\">SeeStack</a></li>\n" +
    "	    <li ng-class=\"{active:$state.includes('snapshot.trace')}\"><a ui-sref=\"snapshot.trace\">Raw Trace</a></li>\n" +
    "    </ul>\n" +
    "\n" +
    "    <div class=\"push-down\" ui-view></div>\n" +
    "	\n" +
    "</div>");
}]);

angular.module("snapshot/trace.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("snapshot/trace.tpl.html",
    "<pre>{{snapshot.stacktrace}}</pre>");
}]);

angular.module("stack/seestack.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("stack/seestack.tpl.html",
    "<div class=\"seethread\" ng-show=\"seestack.threads.length == 0\">No threads found to display.</div>\n" +
    "<div class=\"seethread\" ng-repeat=\"thread in seestack.threads\">\n" +
    "	<div>\n" +
    "		<abbr ng-show=\"thread.ref\" class=\"threadName {{thread.importance}}\" title=\"{{seestack.infos[thread.ref].description}}\">{{thread.name}}</abbr> <span\n" +
    "			ng-hide=\"thread.ref\" class=\"threadName {{thread.importance}}\">{{thread.name}}</span>\n" +
    "	</div>\n" +
    "	<div ng-repeat=\"method in thread.methods\" class=\"method\">\n" +
    "		<span ng-show=\"method.package\"> <abbr class=\"{{seestack.infos[method.packageInfo].importance}}\"\n" +
    "			title=\"{{seestack.infos[method.packageInfo].description}}\">{{method.package}}</abbr> <abbr ng-show=\"method.methodInfo\" class=\"{{method.importance}}\"\n" +
    "			title=\"{{seestack.infos[method.methodInfo].description}}\">{{method.rawLineAfterPackage}}</abbr> <span ng-hide=\"method.methodInfo\"\n" +
    "			class=\"{{method.importance}}\">{{method.rawLineAfterPackage}}</span>\n" +
    "		</span>\n" +
    "		<span ng-hide=\"method.package\" class=\"{{method.importance}}\">\n" +
    "			<abbr ng-show=\"method.methodInfo\" class=\"{{method.importance}}\" title=\"{{seestack.infos[method.methodInfo].description}}\">{{method.rawLine}}</abbr>\n" +
    "			<span ng-hide=\"method.methodInfo\" class=\"{{method.importance}}\">{{method.rawLine}}</span>\n" +
    "		</span>\n" +
    "	</div>\n" +
    "</div>");
}]);

angular.module("stack/stack.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("stack/stack.tpl.html",
    "<div class=\"panel\">\n" +
    "	<div class=\"content\">\n" +
    "		<form>\n" +
    "			<label for=\"filterText\">Thread name filter: <input type=\"text\" name=\"trace-filter\" class=\"trace-filter\" ng-model=\"trace.threadname\" id=\"filterText\"/>\n" +
    "				<button class=\"btn btn-primary btn-sm\" ng-click=\"getStack()\">Trace</button>\n" +
    "				<button class=\"btn btn-default btn-sm\" ng-click=\"doClear()\">Clear</button>\n" +
    "			</label>\n" +
    "			<label for=\"activeOnlyBox\"><input type=\"checkbox\" ng-model=\"trace.activeOnly\" id=\"activeOnlyBox\">Show Active Threads Only</label>\n" +
    "		</form>\n" +
    "	</div>\n" +
    "	\n" +
    "	<div ng-hide=\"rawstack == '' || busy || stackMessage\">\n" +
    "	    \n" +
    "	    <ul class=\"nav nav-tabs nav-inner\">\n" +
    "		    <li ng-class=\"{active:$state.includes('stack.seestack')}\"><a ui-sref=\"stack.seestack\">SeeStack</a></li>\n" +
    "		    <li ng-class=\"{active:$state.includes('stack.trace')}\"><a ui-sref=\"stack.trace\">Raw Trace</a></li>\n" +
    "	    </ul>\n" +
    "\n" +
    "	    <div class=\"push-down\" ui-view></div>\n" +
    "\n" +
    "	</div>\n" +
    "\n" +
    "	<div ng-show=\"stackMessage\" class=\"problemTitle\">\n" +
    "		 <span class=\"glyphicons circle_exclamation_mark\"></span> {{stackMessage}}\n" +
    "	</div>\n" +
    "</div>");
}]);

angular.module("stack/trace.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("stack/trace.tpl.html",
    "<div class=\"seethread\" ng-show=\"seestack.threads.length == 0\">No threads found to display.</div>\n" +
    "<pre>{{rawstack}}</pre>");
}]);
