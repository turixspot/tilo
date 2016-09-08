// Declare app level module which depends on filters, and services
var app = angular.module('tilo', [
  'ngSanitize',
  'ngResource',
  'ui.router',
  'LocalStorageModule',
  'tilo.filters',
  'tilo.directives',
  'tilo.dashboard',
  'tilo.analytics',
  'tilo.projects',
  'tilo.users'
])
.config(function ($stateProvider, $urlRouterProvider, $httpProvider) {
	$urlRouterProvider.otherwise('dashboard');
	$httpProvider.interceptors.push('security-interceptor');
})
.config(function (localStorageServiceProvider) {
  localStorageServiceProvider.setPrefix('tilo');
})
.run(function(localStorageService){
	if(!localStorageService.get('shortcuts'))
		localStorageService.set('shortcuts',{})
})

.service('security-interceptor', function($rootScope, $q) {
	var service = this;

	service.request = function(config) {
		return config;
	};

	service.responseError = function(response) {
	    switch (response.status) {
	        case 403:
                $rootScope.$broadcast('unauthorized');
                break;
	        case 400:
	            $rootScope._error = response.data;
	            break;
	    }

	    return $q.reject(response);
	};

    service.response = function(response){
        delete $rootScope._error;
        return response;
    };
})
.controller('MainCtrl', function($rootScope, $window) {
	$rootScope.$on('unauthorized', function() {
		$window.location = './login.html';
	});
})

.factory('Project', function($resource) {
	return $resource('./api/projects/:id');
})
.factory('User', function($resource) {
	return $resource('./api/users/:id');
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