angular.module("templates.common", ["directives/sfMetricWidget.tpl.html"]);

angular.module("directives/sfMetricWidget.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("directives/sfMetricWidget.tpl.html",
    "<div class=\"sf-metric-widget panel panel-default\">\n" +
    "	<div class=\"panel-heading\">\n" +
    "		<span  class=\"pull-right\" ng-if=\"showcontrols\">\n" +
    "			<span class=\"dropdown\" dropdown>\n" +
    "				<span class=\"dropdown-toggle linkish leftMargin\" dropdown-toggle title=\"Refresh Interval\"><span class=\"glyphicons history\"></span>\n" +
    "				<!-- <span ng-hide=\"refreshtime < 1\" class=\"badge\">{{refreshtime}}</span> -->\n" +
    "				<span class=\"caret\"></span></span>\n" +
    "				<ul class=\"dropdown-menu\" dropdown-menu>\n" +
    "				<li>\n" +
    "				<a ng-click=\"setRefresh(0);\" ng-class=\"(refreshtime==0) ? 'highlighted' : ''\">Manually</a>\n" +
    "				</li>\n" +
    "				<li ng-repeat=\"option in refreshOptions\">\n" +
    "				<a ng-click=\"setRefresh(option);\" ng-class=\"(option==refreshtime) ? 'highlighted' : ''\">Every {{option}} Seconds</a>\n" +
    "				</li>\n" +
    "				</ul>\n" +
    "			</span>\n" +
    "			<span class=\"glyphicons refresh leftMargin\" ng-click=\"refresh();\" title=\"Manual Refresh\"></span>	\n" +
    "			<span class=\"glyphicons bin leftMargin\" ng-click=\"gc();\" title=\"Perform Garbage Collection\"></span>	\n" +
    "		</span>\n" +
    "		<h4 class=\"panel-title\">{{title}}</h4> \n" +
    "	</div>\n" +
    "	<!-- <div class=\"panel-body\">\n" +
    "		<div class=\"abstract\" ng-hide=\"details.show\" ng-repeat=\"data in metrics.abstract\">\n" +
    "			<span class=\"badge alert-info pull-right\">{{data.value}}</span> {{data.label}}\n" +
    "		</div>\n" +
    "		<div ng-show=\"details.show\" ng-repeat=\"detail in metrics.details\">\n" +
    "			<div class=\"row\">\n" +
    "				<div class=\"col-sm-4\">{{detail.counter}}</div>\n" +
    "				<div class=\"col-sm-8 text-right\"><span class=\"badge alert-info\">{{detail.pageCount}}</span> @ {{detail.time}}ms</div>\n" +
    "			</div>\n" +
    "		</div> \n" +
    "	</div> -->\n" +
    "	<div class=\"panel-body\">\n" +
    "			<div class=\"row\">\n" +
    "				<div class=\"col-xs-6\">\n" +
    "					<table>\n" +
    "						<tr ng-repeat=\"data in metrics.abstract\">\n" +
    "							<td>{{data.label}} </td> \n" +
    "							<td class=\"text-right\"> \n" +
    "								<span class=\"badge alert-info\" ng-hide=\"data.label === 'Updated'\">{{data.value | formatIfNumber}}</span>\n" +
    "								<span class=\"badge alert-info\" ng-show=\"data.label === 'Updated'\">\n" +
    "									<span ng-hide=\"data.sameDay\">{{data.value | date:'d-MMM'}}</span>\n" +
    "									<span>{{data.value | date:'h:mm a'}}</span>\n" +
    "								</span>\n" +
    "							</td>\n" +
    "						</tr>\n" +
    "					</table>\n" +
    "				</div>\n" +
    "				<div class=\"col-xs-6\" ng-if=\"metrics.details.length\">\n" +
    "					<table>\n" +
    "						<tr ng-repeat=\"detail in metrics.details\">\n" +
    "							<td class=\"text-right\">{{detail.counter}}</td> \n" +
    "							<td> <span class=\"label label-{{detail.color}} label-as-badge leftMargin\">{{detail.pageCount | formatIfNumber}}</span></td>\n" +
    "							<td> @ {{detail.time | formatIfNumber}}ms</td>\n" +
    "						</tr>\n" +
    "					</table>\n" +
    "				</div> \n" +
    "				<div class=\"col-xs-6\" ng-if=\"metrics.info.length\">\n" +
    "					<table>\n" +
    "						<tr ng-repeat=\"detail in metrics.info\">\n" +
    "							<td class=\"text-right\"><span class=\"text-capitalize\">{{detail.item}}</span></td> \n" +
    "							<td><span class=\"leftMargin\">{{detail.value | formatIfNumber}}</span></td>\n" +
    "						</tr>\n" +
    "					</table>\n" +
    "				</div> \n" +
    "			</div> \n" +
    "	</div> \n" +
    "</div> \n" +
    "");
}]);
