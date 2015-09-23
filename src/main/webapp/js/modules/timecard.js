angular.module('tilo.timecard', ['ui.select'])
.filter('property', function() {
	  return function(input, property, value) {
	    var i=0, len=input.length;
	    for (; i<len; i++) {
	      if (input[i][property] == value) {
	        return input[i];
	      }
	    }
	    return null;
	  }
})
.directive('timecard', function() {
  return {
    templateUrl: 'js/modules/timecard.html',
    controller: function($scope, $filter, Preferences, Project, Log) {
    	$scope.selected = moment().startOf('day');
    	$scope.range = [];
    	/*
    	 * Date selector.
    	 */
    	$scope.active = function(date) {
    		return date.format("YYYYMMDD") == $scope.selected.format("YYYYMMDD"); 
    	}
    	
    	$scope.activate = function(date) {
    		return $scope.selected = date;
    	}
    	
    	$scope.nextWeek = function(date) {
    		$scope.selected.add(7, "day");
    		$scope.$emit("update-range");
    	}
    	
    	$scope.prevWeek = function(date) {
    		$scope.selected.subtract(7, "day");
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
    	 * Shortcuts
    	 */
    	$scope.$on("update-shortcuts", function() { 
    		$scope.shortcuts = Preferences.shortcuts();
    	});
    	$scope.$emit("update-shortcuts");
    	
    	$scope.shortcut = function(project, task) {
    		$scope.project.selected = $filter('property')($scope.projects,'name',project);
    		$scope.task.selected = $filter('property')($scope.project.selected.tasks,'name',task);
    	}
    	
    	/*
    	 * Project selection
    	 */
    	$scope.tasks = [];
    	$scope.task = {};
    	$scope.projects = Project.query();
    	$scope.project = { }
    	
    	$scope.updateTasks = function(item, model){
    		$scope.tasks = item.tasks;
    	}
    	
    	$scope.log = function(){
    		var log = new Log();
    		log.timestamp = $scope.selected.format('x');
    		log.user = "anonymous";
    		log.project = $scope.project.selected.name;
    		log.task = $scope.task.selected.name;
    		log.note = $scope.note;
    		log.time = $scope.time;
    		log.$save().then(function(r){
    			Preferences.shortcut(log.project, log.task);
    			$scope.$emit("update-shortcuts");
    		});
    	}
    }
  };
});