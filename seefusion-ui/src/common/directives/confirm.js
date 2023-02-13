angular.module('directives.confirm', [])
	.directive('confirm', [function () {
		return {
			priority: 100,
			restrict: 'A',
			link: {
			pre: function (scope, element, attrs) {
				var msg = attrs.confirm || "Are you sure you want to delete?";
	
				element.bind('click', function (event) {
					if (!confirm(msg)) {
						event.stopImmediatePropagation();
						event.preventDefault();
					}
				});
			}
		}
	};
}]);