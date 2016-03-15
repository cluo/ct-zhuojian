/**
 * Created by wuhaitao on 2016/3/15.
 */
/**
 * Created by wuhaitao on 2016/3/10.
 */
angular.module('anonymous',['ui.router'])
    .config(function($stateProvider){
        $stateProvider.state('anonymous', {
            url: '/anonymous',
            templateUrl: 'app/anonymous/anonymous.html',
            controller: 'AnonymousCtrl'
        });
    })
    .controller('AnonymousCtrl', function($scope){

    });