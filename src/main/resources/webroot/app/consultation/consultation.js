/**
 * Created by wuhaitao on 2016/3/9.
 */
angular.module('consultation',['ui.router'])
    .config(function($stateProvider){
        $stateProvider.state('consultation', {
            url: '/consultation',
            templateUrl: 'app/consultation/consultations.html',
            controller: 'ConsultationCtrl'
        });
    })
    .service('ConsultationService', function($http){
        var service = this;
        service.getConsultations = function(){
            return $http.get('/api/consultation');
        };
    })
    .controller('ConsultationCtrl', function($scope, ConsultationService){
        function getConsultations(){
            ConsultationService.getConsultations()
                .then(function(result){
                    $scope.consultations = result.data;
                },function(error){
                    console.log(error);
                });
        }

        getConsultations();
    });