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

.controller('ProjectCtrl', function ($scope, Project) {
	
	$scope.$on('reset', function(){
		$scope._project = new Project();
		$scope._project.attributes = [];
		$scope._project.tasks = [];
		$scope._list = Project.query();
	})
	
	$scope.addAttribute = function() {
		$scope._project.attributes.push({name:"", value:""});
	};
	
	$scope.removeAttribute = function(index) {
		$scope._project.attributes.splice(index, 1);
	};
	
	$scope.addTask = function() {
		$scope._project.tasks.push({name:""});
	};
	
	$scope.removeTask = function(index) {
		$scope._project.tasks.splice(index, 1);
	};
	
	$scope.save = function(){
		$scope._project.$save().then(function(p){
			$scope.$emit('reset');
		});
	};
	
	$scope.edit = function(edit){
		Project.get({ id: edit }).$promise.then(function(p){
			$scope._project = p;
		});
	};
	
	$scope.remove = function(remove){
		Project.remove({ id: remove }).$promise.then(function(r){
			$scope.$emit('reset');
		});
	};
	
	$scope.$emit('reset');
});