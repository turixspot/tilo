// Declare app level module which depends on filters, and services
var app = angular.module('tilo', [
  'ngSanitize', 
  'ui.router',
  'tilo.dashboard'
])
.config(function ($stateProvider, $urlRouterProvider) {
	    $urlRouterProvider.otherwise('dashboard');
});
/*.run(["$templateCache", function($templateCache) {
	var options = "<a href='' ng-click=''>Create new</a>";
	$templateCache.put("bootstrap/choices.tpl.html","<ul class=\"ui-select-choices ui-select-choices-content ui-select-dropdown dropdown-menu\" role=\"listbox\" ><li class=\"ui-select-choices-group\" id=\"ui-select-choices-{{ $select.generatedId }}\"><div class=\"divider\" ng-show=\"$select.isGrouped && $index > 0\"></div><div ng-show=\"$select.isGrouped\" class=\"ui-select-choices-group-label dropdown-header\" ng-bind=\"$group.name\"></div><div id=\"ui-select-choices-row-{{ $select.generatedId }}-{{$index}}\" class=\"ui-select-choices-row\" ng-class=\"{active: $select.isActive(this), disabled: $select.isDisabled(this)}\" role=\"option\"><a href=\"javascript:void(0)\" class=\"ui-select-choices-row-inner\"></a></div></li>"+options+"</ul>");
}]);*/