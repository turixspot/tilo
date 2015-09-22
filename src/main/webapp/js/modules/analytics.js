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

.controller('AnalyticsCtrl', function ($scope, Analytics) {
	/**
	 * Projects.
	 */
	var projects = {};
	projects.type = "PieChart";
	projects.data = [['Project', 'time']];
    projects.options = {
        width: 400,
        height: 200,
        chartArea: {left:10,top:10,bottom:0,height:"100%"},
        pieHole: 0.6,
        legend: {position: 'none'}
    };

    $scope.projects = projects;
    
    Analytics.overview({from:"0", to:"9223372036854775807"}).$promise.then(function(tmp){
    	angular.forEach(tmp.aggregations.byproject, function(value, key) {
    		projects.data.push([key , value]);
    	});    	
    });
    
    
    var chart1 = {};
    chart1.type = "BarChart";
    chart1.data = [
       ['user', 'semana', 'guiamais', 'publicar'],
       ['rvega', 10, 50, 30],
       ['ggallardo', 80, 15, 50]
      ];
    chart1.data.push(['aprieto',2, 20, 50]);
    chart1.options = {
        width: 600,
        height: 200,
        isStacked: true,
        bar: { groupWidth: '75%' }
    };

    $scope.chart = chart1;

});