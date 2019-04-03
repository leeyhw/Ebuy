//服务层
app.service('collectService',function($http){

    this.findCollectList = function () {
        return $http.get('../collect/findCollectList.do')
    }
	
});