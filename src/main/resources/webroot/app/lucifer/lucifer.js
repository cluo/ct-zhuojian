/**
 * Created by wuhaitao on 2016/2/25.
 */
var userModule = angular.module('helloworld', ['ui.router', 'ngAnimate', 'ngTouch']);
userModule
    .config(function ($stateProvider) {
        $stateProvider.state('ctct', {
            url: '/index',
            templateUrl: 'app/lucifer/galary.html',
            controller: 'HelloWorld'
        });
    })
    .controller('HelloWorld', function ($scope) {
        $scope.data="abc edf";
    });