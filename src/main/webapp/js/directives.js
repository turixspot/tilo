angular.module('tilo.directives', [])

.directive('validateMsg', function($compile) {
    return {
      restrict: 'A',
      compile: function (element) {
      	return function(scope, element){
      		var path = element.attr('id') ? element.attr('id') : "global";
      		$compile('<span class="text-danger" style="display: block" ng-repeat="msg in _error.details[\''+path+'\']">{{msg}}</span><br>')(scope, function (clone) {
      			element.after(clone);
      		});
      	}
      }
    };
})