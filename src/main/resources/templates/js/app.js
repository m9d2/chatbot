var app = angular.module('wx', []);

app.controller('autoReply', function ($scope, $http) {
    $scope.isLogin = false;
    $scope.showMain = false;
    $scope.ipInfo = remote_ip_info;
    $scope.code = '';
    $http({
        method: 'GET',
        url: 'iamge'
    }).then(function success(response) {
        $scope.imageUrl = response.data.data;
        $http({
            method: 'GET',
            url: 'login',
        }).then(function success() {
            $scope.code = response.data.code;
            $scope.isLogin = true;
            $http({
                method: 'GET',
                url: 'start',
            }).then(function success() {

            });
        });
    });
});

