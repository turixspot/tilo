angular.module('tilo.timecard', ['ui.select', 'angular-input-interval'])
.directive('timecard', function() {
  return {
    templateUrl: 'js/modules/timecard.html',
    controller: function($scope, $filter, Preferences, Project, Log) {
    	$scope.selected = moment().startOf('day');
    	$scope.range = [];
    	/**
    	 * Date selector.
    	 */
    	$scope.active = function(date) {
    		return date.format("YYYYMMDD") == $scope.selected.format("YYYYMMDD"); 
    	}
    	
    	$scope.activate = function(date) {
    		$scope.selected = date;
    		$scope.$emit("update-logs");
    	}
    	
    	$scope.nextWeek = function(date) {
    		$scope.activate($scope.selected.clone().add(7, "day"));
    		$scope.$emit("update-range");
    	}
    	
    	$scope.prevWeek = function(date) {
    		$scope.activate($scope.selected.clone().subtract(7, "day"));
    		$scope.$emit("update-range");
    	}
    	
    	$scope.$on("update-range", function() { 
    		$scope.range = [];
	    	var range = moment.twix($scope.selected.clone().isoWeekday(1), $scope.selected.clone().isoWeekday(7)).iterate("days");
	    	while(range.hasNext())
	    	    $scope.range.push(range.next());
    	});
    	
    	$scope.$emit("update-range");
    	
    	/**
    	 * Project/Task selection
    	 */
    	$scope.task = {};
    	$scope.projects = Project.query();
    	$scope.project = { }
    	
    	$scope.updateTasks = function(item, model){
    		$scope.tasks = item.tasks;
    		$scope.task.selected = null;
    	}

    	/**
    	 * Shortcuts
    	 */
    	$scope.$on("update-shortcuts", function() { 
    		$scope.shortcuts = Preferences.shortcuts();
    	});
    	$scope.$emit("update-shortcuts");
    	
    	$scope.shortcut = function(project, task) {
    		$scope.project.selected = $filter('filter')($scope.projects, {'name': project})[0];
    		$scope.task.selected = $filter('filter')($scope.project.selected.tasks, {'name' : task})[0];
    	}
    	
    	/**
    	 * Logs
    	 */
    	$scope.$on("update-logs", function() { 
    		Log.query({date: $scope.selected.format('x'), user: $scope.user}).$promise.then(function(r) {
    			 $scope.logs = r;
    		});
    	});
    	
    	$scope.remove = function(remove){
    		Log.remove({ id: remove }).$promise.then(function(r){
    			$scope.$emit('update-logs');
    		});
    	};
    	
    	
    	$scope.$on("reset-form", function() {
    		$scope.project.selected = null;
    		$scope.task.selected = null;
    		$scope.note = null;
    		$scope.time = null;
    	});
    	
    	
    	$scope.log = function(){

    		var log = new Log();
    		var timestamp = moment();
    		timestamp.date($scope.selected.date());
    		timestamp.month($scope.selected.month());
    		timestamp.year($scope.selected.year());
    		log.timestamp = timestamp.format('x');
    		log.user = $scope.user;
    		log.project = $scope.project.selected != null ? $scope.project.selected.name : null;
    		log.task = $scope.task.selected != null ? $scope.task.selected.name : null;
    		log.note = $scope.note;
    		log.time = $scope.time;
    		
    		log.$save().then(function(r){
    			Preferences.shortcut(log.project, log.task);
    			$scope.$emit("update-shortcuts");
    			$scope.$emit("update-logs");
    			$scope.$emit("reset-form");
    		});
    	}
    	
    	$scope.$watch('user', function(){
    		$scope.$emit("update-logs");
    	});
    }
  };
});