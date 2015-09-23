// Declare app level module which depends on filters, and services
var app = angular.module('tilo', [
  'ngSanitize',
  'ngResource',
  'ui.router',
  'LocalStorageModule',
  'tilo.dashboard',
  'tilo.analytics',
  'tilo.projects'
])
.config(function ($stateProvider, $urlRouterProvider) {
	$urlRouterProvider.otherwise('dashboard');
})
.config(function (localStorageServiceProvider) {
  localStorageServiceProvider.setPrefix('tilo');
})
.run(function(localStorageService){
	if(!localStorageService.get('shortcuts'))
		localStorageService.set('shortcuts',{})
})
.factory('Project', function($resource) {
	return $resource('./api/projects/:id');
})
.factory('Log', function($resource) {
	return $resource('./api/logs/:id');
})
.factory('Analytics', function($resource) {
	return $resource('./api/analytics/:report/:from/:to/', {},{
		overview : {
            method: 'GET',
            isArray: false,
            params: {
                report: 'overview'
            }
        }
	});
})
.factory('Preferences', function(localStorageService){
	return {
		shortcut : function(project, task){
			var s = localStorageService.get('shortcuts');
			
			if(!s[project])
				s[project] = [task];
			
			if(s[project].indexOf(task) < 0)
				s[project].push(task);

			localStorageService.set('shortcuts', s);
		},
		
		shortcuts : function() {
			return localStorageService.get('shortcuts');
		}
	}
});