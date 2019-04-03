app.controller('collectController', function ($scope, $controller, collectService) {
    $controller('baseController', {$scope: $scope});

    //查询收藏商品
    $scope.findCollectList = function () {
        collectService.findCollectList().success(
            function (response) {
                $scope.collectList = response;
            }
        )
    }



});