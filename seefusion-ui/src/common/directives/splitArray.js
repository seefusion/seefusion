angular.module('directives.splitArray', [])
    .directive('splitArray', [function () {
    return {
        restrict: 'A',
        require: 'ngModel',
        link: function(scope, element, attr, ngModel) {

            function unsquish(text) {
                return text.split("\n");
            }

            function squish(array) {                        
                return array.join("\n");
            }

            ngModel.$parsers.push(unsquish);
            ngModel.$formatters.push(squish);
        }
    };
}]);