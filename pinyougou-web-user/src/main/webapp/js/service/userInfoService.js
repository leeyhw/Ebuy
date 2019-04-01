//服务层
app.service('userInfoService',function($http){
	this.addUserInfo=function(infoMap){
		return $http.post('../user/addUserInfo.do',infoMap);
	};

    //上传头像
    this.uploadFile = function(){

        // 向后台传递数据:
        var formData = new FormData();
        // 向formData中添加数据:
        formData.append("file",file.files[0]);
        return $http({
            method:'post',
            url:'../user/uploadFile.do',
            data:formData,
            headers:{'Content-Type':undefined} ,// Content-Type : text/html  text/plain multipart/form-data
            transformRequest: angular.identity
        });
    }
});