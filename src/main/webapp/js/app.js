// Declare app level module which depends on filters, and services
var app = angular.module('tilo', [
  'ngSanitize',
  'ngResource',
  'ui.router',
  'tilo.dashboard',
  'tilo.projects'
])
.config(function ($stateProvider, $urlRouterProvider) {
	    $urlRouterProvider.otherwise('dashboard');
})
.factory('Project', function($resource) {
  return $resource('./api/projects/:id');
});