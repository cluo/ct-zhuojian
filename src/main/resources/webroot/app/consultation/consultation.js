/**
 * Created by wuhaitao on 2016/3/9.
 */
angular.module('consultation',['ui.router', 'record', 'ct.area'])
    .config(function($stateProvider){
        $stateProvider.state('consultation', {
            url: '/consultation',
            templateUrl: 'app/consultation/consultations.html',
            controller: 'ConsultationCtrl'
        })
        .state('ctImages', {
            url: '/consultation/:id',
            templateUrl: 'app/consultation/ctimages.html',
            controller: 'CTImageCtrl'
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
    })
    .service('CTImageService', function($http){
        var service = this;
        service.getCTImages = function(id){
            return $http.get('/api/consultation/'+id);
        };
    })
    .controller('CTImageCtrl', function($scope, $state, $stateParams, CTImageService){
        var id = $stateParams.id;
        function getCTImages(consultationId){
            CTImageService.getCTImages(consultationId)
                .then(function(result){
                    $scope.ctImages = result.data;
                },function(error){
                    console.log(error);
                });
        }

        $scope.goCAD = function(ctImage){
            console.log(ctImage);
            $state.go('cad', {
                'id':ctImage.id,
                'type':ctImage.type,
                'file':ctImage.file,
                'diagnosis':ctImage.diagnosis,
                'consultationId':ctImage.consultationId
            });
        };
        getCTImages(id);
    })
    .filter('type', function(){
        return function(e){
            var out = [];
            for(var i=0;i< e.length;i++){
                var t = e[i].type;
                console.log(t);
                if(t == 1){
                    e[i].type = "肝脏";
                }
                else{
                    e[i].type = "肺部";
                }
                out.push(e[i]);
            }
            return out;
        }
    });