/**
 * Created by wuhaitao on 2016/2/25.
 */
var userModule = angular.module('lung', ['ui.router', 'ngAnimate', 'ngTouch']);
userModule
    .config(function ($stateProvider) {
        $stateProvider.state('ctct', {
            url: '/lung',
            templateUrl: 'app/lucifer/table.html',
            controller: 'HelloWorld'
        });
    })
    .controller('HelloWorld', function ($scope) {
        $scope.data="abc edf";
    });