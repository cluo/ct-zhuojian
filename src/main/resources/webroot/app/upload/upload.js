/**
 * Created by wuhaitao on 2016/3/8.
 */
angular.module('upload', ['ui.router','ngFileUpload'])
    .config(function ($stateProvider) {
        $stateProvider
            .state('upload', {
                url: '/upload',
                templateUrl: 'app/upload/upload.html',
                controller: 'UploadCtrl',
            });
    })
    .controller('UploadCtrl', ['$scope', 'Upload', function ($scope, Upload) {
        // upload later on form submit or something similar
        $scope.submit = function() {
            if ($scope.form.file.$valid && $scope.file) {
                $scope.upload($scope.file);
            }
        };

        // upload on file select or drop
        $scope.upload = function (file) {
            Upload.upload({
                url: '/upload',
                data: {file: file, 'username': 'wuhaitao'}
            }).then(function (resp) {
                console.log('Success ' + resp.config.data.file.name + 'uploaded. Response: ' + resp.data);
            }, function (resp) {
                console.log('Error status: ' + resp.status);
            }, function (evt) {
                var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                console.log('progress: ' + progressPercentage + '% ' + evt.config.data.file.name);
            });
        };
    }])