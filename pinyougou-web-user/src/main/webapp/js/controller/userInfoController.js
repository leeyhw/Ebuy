app.controller('userInfoController', function ($scope, $controller, userInfoService) {

    $controller('baseController', {$scope: $scope});

    //定义参数数组
    $scope.infoMap = {'nickName': '', 'gender': '0', 'birthday': '', 'address': '', 'job': '', 'picPath': ''};

    //修改用户信息
    $scope.updateUserInfo = function () {
        userInfoService.updateUserInfo($scope.infoMap).success(
            function (response) {
                if (response.flag) {
                    alert(response.message);
                    window.location.href = 'home-index.html';
                } else {
                    alert(response.message);
                }
            }
        );
    };

    $scope.checkTable = function () {
        $scope.infoMap.birthday = $("#select_year2").val() + '-' + $("#select_month2").val() + '-' + $("#select_day2").val();
        $scope.infoMap.address = $("#province1").val() + '/' + $("#city1").val() + '/' + $("#district1").val();
        $scope.infoMap.job = $("#inputJob").val();
        $scope.infoMap.nickName = ($("#inputName").val());

        if ($scope.infoMap.nickName == '' || $scope.infoMap.nickName.trim() == '') {
            alert("昵称不能为空");
            return;
        }
        if ($scope.infoMap.gender == "0") {
            alert("请选择性别");
            return;
        }

        $scope.updateUserInfo();


    }

    /*$scope.setPicPath = function () {
        if ($scope.infoMap.picPath==''){
            $scope.picPath="img/_/photo_icon.png";
        }
    };*/

    //监听器  input框内容变化  如有变化 及时上传 进行img加载显示
    $("#file").bind("input", function () {
        $scope.uploadFile();
    });


    //上传头像 (取消按钮上传 效果快 取消判断空文件)
    $scope.uploadFile = function () {
        // 调用userInfoService的方法完成文件的上传
        userInfoService.uploadFile().success(function (response) {
            if (response.flag) {
                // 获得url   <img src="{{infoMap.picPath}}" -->再次发出请求直接获取图片本身
                $scope.infoMap.picPath = response.message;
            } else {
                alert(response.message);
            }
        });
    }

});