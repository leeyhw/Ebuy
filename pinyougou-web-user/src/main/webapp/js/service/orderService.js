//服务层
app.service('orderService',function($http){

	//读取列表数据绑定到表单中
	this.findOrder=function(pageMap){
		return $http.get('../order/findOrder.do?page='+pageMap.pageNo+"&rows="+pageMap.pageSize);
        //return $http.get('../itemCat/findPage.do?page='+page+'&rows='+rows);
	};

});