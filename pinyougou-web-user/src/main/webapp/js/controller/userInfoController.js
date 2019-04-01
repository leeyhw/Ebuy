
app.controller('userInfoController', function ($scope, userInfoService, loginService) {

    $scope.showName = function () {
        loginService.showName().success(
            function (response) {
                $scope.loginName = response.loginName;
            }
        );

    };

    //定义参数数组
    $scope.infoMap={'nickName':'','gender':'0','birthday':'','address':'','job':'','picPath':''};

    $scope.addUserInfo = function () {
        userInfoService.addUserInfo($scope.infoMap).success(
            function (response) {
                if (response.flag){
                    alert(response.message);
                }else {
                    alert(response.message);
                }
            }
        );
    };

    $scope.checkTable = function(){
        $scope.infoMap.birthday = $("#select_year2").val()+'-'+$("#select_month2").val()+'-'+$("#select_day2").val();
        $scope.infoMap.address = $("#province1").val()+'/'+$("#city1").val()+'/'+$("#district1").val();
        $scope.infoMap.job = $("#inputJob").val();
        $scope.infoMap.nickName = ($("#inputName").val());
        $scope.infoMap.picPath = $("#up_img_WU_FILE_0").val();

        if ($scope.infoMap.nickName=='' || $scope.infoMap.nickName.trim()==''){
            alert("昵称不能为空");
            return;
        }
        if ($scope.infoMap.gender=="0"){
            alert("请选择性别");
            return;
        }

        $scope.addUserInfo();


    }

    $scope.setPicPath = function () {
        if ($scope.infoMap.picPath==''){
            $scope.picPath="img/_/photo_icon.png";
        }
    };

    // //监听器  input框内容变化
    // $("#file").bind("input",function(){
    //     alert(111)
    //     $scope.picPath = $("#file").val();
    //     alert($scope.picPath)
    // });



//img/_/photo_icon.png
    $scope.uploadFile = function(){
        if (!$("#file").val()){
            alert("请先选择文件")
            return;
        }
        // 调用userInfoService的方法完成文件的上传
        userInfoService.uploadFile().success(function(response){
            if(response.flag){
                // 获得url   <img src="{{infoMap.picPath}}" -->再次发出请求直接获取图片本身
                $scope.infoMap.picPath =  response.message;//http://...
            }else{
                alert(response.message);
            }
        });
    }

});