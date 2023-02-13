angular.module('directives.toBoolean', [])
    .directive('toBoolean', [function () {
    return {
        restrict: 'A',
        require: 'ngModel',
        link: function(scope, element, attr, ngModel) {

            function makeBoolean(text) {
                var b = (text === 'true'); 
                return b;
            }

            function orig(text) {return text;}

            ngModel.$parsers.push(makeBoolean);
            ngModel.$formatters.push(orig);
        }
    };
}]);