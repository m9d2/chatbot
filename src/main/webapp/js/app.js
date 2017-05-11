var app = angular.module('wx', []);

app.controller('autoReply', function ($scope, $http) {
    $scope.isLogin = false;
    $scope.ipInfo = remote_ip_info;
    $scope.code = '';
    $http({
        method: 'GET',
        url: 'iamge.do'
    }).then(function success(response) {
        $scope.imageUrl = response.data.data.url;
        $scope.uuid = response.data.data.uuid;
        $http({
            method: 'POST',
            url: 'login.do',
            data: $scope.uuid
        }).then(function success(response) {
            $scope.isLogin = true;
            $scope.code = response.data.code;
            $scope.wechat = response.data.data.Wechat;
            $scope.wechat.province = $scope.ipInfo.province;
            $scope.wechat.city = $scope.ipInfo.city;
        }).then(function success(response) {
            $http({
                method: 'POST',
                url: 'start.do',
                data: $scope.wechat
            }).then(function success(response) {

            });
        });
    });
});

