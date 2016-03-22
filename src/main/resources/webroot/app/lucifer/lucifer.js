/**
 * Created by wuhaitao on 2016/2/25.
 */
var userModule = angular.module('lung', ['ui.router', 'ngAnimate', 'ngTouch']);
userModule
    .config(function ($stateProvider) {
        $stateProvider.state('ctct', {
            url: '/lung/:id',
            templateUrl: 'app/lucifer/galary.html',
//            controller: 'HelloWorld'
        });
    })
    .controller('HelloWorld', function ($scope, $stateParams, $http) {
    console.log($stateParams);
        $http.get('/api/lung/'+$stateParams.id)
            .then(function(result){
                $scope.val = result.data;
            },function(error){
                console.log(error);
            })
        })
    ;