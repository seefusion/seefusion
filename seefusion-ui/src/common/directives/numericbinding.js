angular.module('directives.numericbinding', [])
  .directive('numericbinding', function () {
    return {
        restrict: 'A',
        require: 'ngModel',
        scope: {
            model: '=ngModel',
        },                
       link: function (scope, element, attrs, ngModelCtrl) {
           if (scope.model && typeof scope.model === 'string') {
               scope.model = parseInt(scope.model);
           }                  
        }
    };
});