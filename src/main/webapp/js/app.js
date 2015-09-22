// Declare app level module which depends on filters, and services
var app = angular.module('tilo', [
  'ngSanitize',
  'ngResource',
  'ui.router',
  'tilo.dashboard',
  'tilo.analytics',
  'tilo.projects'
])
.config(function ($stateProvider, $urlRouterProvider) {
	$urlRouterProvider.otherwise('dashboard');
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
});