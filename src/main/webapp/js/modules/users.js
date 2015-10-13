'use strict';

angular.module('tilo.users', [])

.config(function ($stateProvider, $urlRouterProvider) {
        $stateProvider
            .state('users', {
                url: '/users',
                templateUrl: 'js/modules/users.html',
                controller: 'UserCtrl'
            });
})

.controller('UserCtrl', function ($scope, User) {
	
	$scope.$on('reset', function(){
		$scope._user = new User();
		$scope._list = User.query();
		$scope.opts = {};
		$scope.opts.passUpdate = true;
		$scope.opts.isEdit = false;
		$scope.opts.isAdmin = false;
	})
	
	$scope.save = function(){
		delete $scope._user.passwordRewrite;
		
		if($scope.opts.isAdmin) {
			$scope._user.roles = ["administrator"];
		} else {
			$scope._user.roles = [];
		}
		
		$scope._user.$save().then(function(p){
			$scope.$emit('reset');
		});
	};
	
	$scope.edit = function(edit){
		User.get({ id: edit }).$promise.then(function(p){
			$scope._user = p;
			$scope.opts.isAdmin = p.roles.indexOf("administrator") != -1;
			$scope.opts.passUpdate = false;
			$scope.opts.isEdit = true;
		});
	};
	
	$scope.remove = function(remove){
		User.remove({ id: remove }).$promise.then(function(r){
			$scope.$emit('reset');
		});
	};
	
	$scope.$emit('reset');
});