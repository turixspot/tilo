'use strict';

angular.module('tilo.dashboard', ["tilo.timecard"])

.config(function ($stateProvider, $urlRouterProvider) {
        $stateProvider
            .state('dashboard', {
                url: '/dashboard',
                templateUrl: 'js/modules/dashboard.html',
                controller: 'DashboardCtrl'
            });
})

.controller('DashboardCtrl', function ($scope) {
	$scope.project = {};
	$scope.projects = [{name:"project A"},{name:"project B"},{name:"project C"}];
	$scope.refreshProject = function(search){
		if(search.length > 0) {
			if($scope.projects[0].create)
				$scope.projects[0].name = search;
			else
				$scope.projects.splice(0, 0, {name: search, create: true});
			
			for (var i = $scope.projects.length - 1; i > 0; i--) {
			    if ($scope.projects[i].name === search) {
			    	$scope.projects.shift();
			    	break;
			    }
			}
		} else {
			if($scope.projects[0].create)
				$scope.projects.shift();
		}
	};
	
	$scope.createProject = function(project) {
		alert(project);
	}
});