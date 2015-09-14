// Declare app level module which depends on filters, and services
var app = angular.module('tilo', [
  'ngSanitize', 
  'ui.router',
  'tilo.dashboard',
  'tilo.projects'
])
.config(function ($stateProvider, $urlRouterProvider) {
	    $urlRouterProvider.otherwise('dashboard');
});