var app = angular.module('wx', []);

app.controller('autoReply', function ($scope, $http) {
    $scope.isLogin = false;
    $scope.showMain = false;
//    $scope.ipInfo = remote_ip_info;
    $scope.code = '';
    $http({
        method: 'GET',
        url: 'iamge.do'
    }).then(function success(response) {
        $scope.imageUrl = response.data.data;
        $http({
            method: 'GET',
            url: 'login.do',
        }).then(function success() {
            $http({
                method: 'GET',
                url: 'start.do',
            }).then(function success() {
            	$scope.isLogin = true;
            });
        });
    });
});

