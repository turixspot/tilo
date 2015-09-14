'use strict';

angular.module('tilo.projects', [])

.config(function ($stateProvider, $urlRouterProvider) {
        $stateProvider
            .state('projects', {
                url: '/projects',
                templateUrl: 'js/modules/projects.html',
                controller: 'ProjectCtrl'
            });
})

.controller('ProjectCtrl', function ($scope) {
	$scope._project = {name:"", attributes:[], tasks:[]};
	$scope._list = [];
	
	$scope.addAttribute = function() {
		$scope._project.attributes.push({id:"", name:"", value:""});
	};
	
	$scope.removeAttribute = function(index) {
		$scope._project.attributes.splice(index, 1);
	};
	
	$scope.addTask = function() {
		$scope._project.tasks.push({id:"", name:""});
	};
	
	$scope.removeTask = function(index) {
		$scope._project.tasks.splice(index, 1);
	};
	
	$scope.save = function(){
		$scope._list.push($scope._project);
		$scope._project = {name:"", attributes:[], tasks:[]};
	}
});