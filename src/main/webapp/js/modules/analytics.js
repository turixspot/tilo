'use strict';

angular.module('tilo.analytics', ['googlechart'])

.config(function ($stateProvider, $urlRouterProvider) {
        $stateProvider
            .state('analytics', {
                url: '/analytics',
                templateUrl: 'js/modules/analytics.html',
                controller: 'AnalyticsCtrl'
            });
})

.directive('rangePicker', function() {
      var directive = {
        restrict: 'A',
        require: '?ngModel',
        scope: {
            options: "="
        },
        link: function(scope, element, attrs, modelCtrl) {
        	
        	var options = angular.extend({}, { separator: ' - ', cancelLabel: 'Clear' }, scope.options);
        	
        	var _picker = null;
        	
        	var clear = function() {
                _picker.data('daterangepicker').setStartDate();
                _picker.data('daterangepicker').setEndDate();
                return element.val('');
            };

        	var _formatted = function(viewVal) {
				var f;
				f = function(date) {
					if (!moment.isMoment(date)) {
						return moment(date).format(options.locale.format);
					}
					return date.format(options.locale.format);
				};
				if (options.singleDatePicker) {
					return f(viewVal.startDate);
				} else {
					return [ f(viewVal.startDate),
							f(viewVal.endDate) ]
							.join(options.separator);
				}
			};
			var _setStartDate = function(newValue) {
				return setTimeout(function() {
					var m;
					if (_picker) {
							m = moment(newValue);
							if (_picker.endDate < m) {
								_picker.data('daterangepicker').setEndDate(m);
							}
							return _picker.data('daterangepicker').setStartDate(m);
					}
				});
			};
			var _setEndDate = function(newValue) {
				return setTimeout(function() {
					var m;
					if (_picker) {
							m = moment(newValue);
							if (_picker.startDate > m) {
								_picker.data('daterangepicker').setStartDate(m);
							}
							return _picker.data('daterangepicker').setEndDate(m);
					}
				});
			};

			scope.$watch('model.startDate', function(newValue) {
				return _setStartDate(newValue);
			});
			scope.$watch('model.endDate', function(newValue) {
				return _setEndDate(newValue);
			});
            
            modelCtrl.$formatters.push(function(val) {
                if (val && val.startDate && val.endDate) {
                  _setStartDate(val.startDate);
                  _setEndDate(val.endDate);
                  return val;
                }
                return '';
            });
            
            modelCtrl.$parsers.push(function(val) {
            	console.log(JSON.stringify(val))
            	if (!angular.isObject(val) || !(val.hasOwnProperty('startDate') && val.hasOwnProperty('endDate'))) {
                    return modelCtrl.$modelValue;
                }
                return val;
            });
        	
        	modelCtrl.$render = function() {        		
	        	if (!modelCtrl.$modelValue)
	        		return element.val('');
	
	        	if (modelCtrl.$modelValue.startDate === null)
	        		return element.val('');
	    
	        	return element.val(_formatted(modelCtrl.$modelValue));
        	};
        	
        	_picker = angular.element(element[0]).daterangepicker(options, function(start, end, label) {
        		console.log("New date range selected: ' + start.format('YYYY-MM-DD') + ' to ' + end.format('YYYY-MM-DD') + ' (predefined range: ' + label + ')");
        		setTimeout(function() {
        			return modelCtrl.$setViewValue({
        				startDate: start,
        				endDate: end
        			});
        		});
        		return modelCtrl.$render();
        	});
        	
        	setTimeout(function() {
    			return modelCtrl.$setViewValue({
    				startDate: options.startDate,
    				endDate: options.endDate
    			});
    		});
        }
      };
      return directive;
})

.controller('AnalyticsCtrl', function ($scope, $filter, Analytics) {
	/**
	 * Range picker.
	 */
	$scope.range = { startDate: moment().subtract(30, "day"), endDate: moment()};
	$scope.picker = {};
	$scope.picker.ranges = {
	        	"Today": [
      	            moment(),
      	            moment()
      	        ],
      	        "Last 7 Days": [
      	            moment().subtract(7, "day"),
      	            moment()
      	        ],
      	        "Last 30 Days": [
      	            moment().subtract(30, "day"),
      	            moment()
      	        ],
      	        "This Month": [
      	            moment().startOf("month"),
      	            moment().endOf("month"),
      	        ],
      	        "Last Month": [
       	            moment().subtract(1, "month").startOf("month"),
      	            moment().subtract(1, "month").endOf("month"),
      	        ]
      	    };
	$scope.picker.locale = { format: 'YYYY-MM-DD'};
	$scope.picker.startDate = $scope.range.startDate;
	$scope.picker.endDate = $scope.range.endDate;

	$scope.$watch('range', function(value){
		if(value)
			$scope.$emit("update-analytics");
	});
	
	/**
	 * Projects.
	 */
	$scope.$on("update-analytics", function(){
	    
	    Analytics.overview({from: $scope.range.startDate.format('x'), to: $scope.range.endDate.format('x')}).$promise.then(function(tmp){
	    	
	    	$scope.results = tmp.results;
	    	if(!tmp.results)
	    		return;
	    	
	    	
	    	var projects = {};
	    	projects.type = "PieChart";
	    	projects.data = [["Project", "Hours"]];
	    	projects.options = {
	    			width: 400,
	    			height: 200,
	    			chartArea: {left:10,top:10,bottom:0,height:"100%"},
	    			pieHole: 0.6,
	    			legend: {position: 'none'}
	    	};	    	
	    	angular.forEach(tmp.byproject.buckets, function(item) {
	    		projects.data.push([item.key , item.time_ms.value / (1000*60*60)]);
	    	});    	
	    	$scope.projects = projects;
	    	
	    	var users = {};
	    	users.type = "BarChart";
	    	users.data = [["User", "Hours"]];
	    	users.options = {
	    			width: 500,
	    			height: 200,
	    			bar: { groupWidth: '75%' },
	    			legend: {position: 'none'}
	    	};
	    	angular.forEach(tmp.byuser.buckets, function(item) {
	    		users.data.push([item.key , item.time_ms.value / (1000*60*60)]);
	    	});
	    	$scope.users = users;

	    	$scope.detailed = tmp.detailed;
	    });
	});
});