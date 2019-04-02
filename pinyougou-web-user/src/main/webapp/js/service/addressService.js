//服务层
app.service('addressService',function($http){

	//查询地址集合
    this.findAdressList = function () {
		return $http.get('../address/findAddressListByUserId.do');
    };

    //查询省级分类
    this.findProvinceList = function () {
		return $http.get('../address/findProvinceList.do');
    };

    //查询市级分类
    this.findByProvinceId = function (provinceId) {
		return $http.get('../address/findByProvinceId.do?provinceId='+provinceId);
    };

    //查询区级分类
	this.findByCityId = function (cityId) {
		return $http.get('../address/findByCityId.do?cityId='+cityId);
    };

	//新增地址
    this.addAddress = function (entity) {
        return $http.post('../address/addAddress.do',entity);
    };

    //修改地址
	this.updateAddress = function (entity) {
        return $http.post('../address/updateAddress.do',entity);
    };

	//回显
	this.findOne = function (id) {
		return $http.get('../address/findOne.do?id='+id)
    }

    //删除一条地址
	this.delOneAddress = function (id) {
		return $http.get('../address/delOneAddress.do?id='+id)
    }

    //设置默认地址
	this.setDefault = function (id) {
        return $http.get('../address/setDefault.do?id='+id)
    }


	
});