angular.module('tilo.timecard', [])
.directive('timecard', function() {
  return {
    templateUrl: 'js/modules/timecard.html',
    controller: function($scope, Project, Log) {
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
    	
    	/*
    	 * Project selection
    	 */
    	$scope.task = {};
    	$scope.projects = Project.query();
    	$scope.project = $scope.projects.length > 0 ? $scope.projects[0] : { tasks : []};
    	
    	$scope.log = function(){
    		var log = new Log();
    		log.timestamp = $scope.selected.format('x');
    		log.user = "anonymous";
    		log.project = $scope.project.name;
    		log.task = $scope.task.name;
    		log.note = $scope.note;
    		log.time = $scope.time;
    		log.$save().then(function(r){
    			alert(r);
    		});
    	}
    }
  };
});