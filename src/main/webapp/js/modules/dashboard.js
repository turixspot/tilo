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

});