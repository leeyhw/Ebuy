//首页控制器
app.controller('indexController',function($scope,loginService,orderService){
	$scope.showName=function(){

			loginService.showName().success(
					function(response){
						$scope.loginName=response.loginName;
                        $scope.headPhoto = response.headPhoto;
					}
			);
	};

    $scope.headPhoto='';

    //定义搜索对象的结构
    $scope.pageMap={'pageNo':1,'pageSize':3};

    //搜索
    $scope.findOrder=function(){

        $scope.pageMap.pageNo= parseInt($scope.pageMap.pageNo);//转换为数字
        //入参： Map
        //URL
        orderService.findOrder($scope.pageMap).success(
            function(response){
                $scope.resultMap=response;//返回值还是Map
                // 结果集
                // 总页
                // 总条数
                buildPageLabel();//构建分页栏
                //$scope.searchMap.pageNo=1;//查询后显示第一页
            }
        );
    }

    //构建分页栏
    buildPageLabel=function(){
        //构建分页栏
        $scope.pageLabel=[];
        var firstPage=1;//开始页码
        var lastPage=$scope.resultMap.totalPages;//截止页码
        $scope.firstDot=true;//前面有点
        $scope.lastDot=true;//后边有点

        if($scope.resultMap.totalPages>5){  //如果页码数量大于5

            if($scope.pageMap.pageNo<=3){//如果当前页码小于等于3 ，显示前5页
                lastPage=5;
                $scope.firstDot=false;//前面没点
            }else if( $scope.pageMap.pageNo>= $scope.resultMap.totalPages-2 ){//显示后5页
                firstPage=$scope.resultMap.totalPages-4;
                $scope.lastDot=false;//后边没点
            }else{  //显示以当前页为中心的5页
                firstPage=$scope.pageMap.pageNo-2;
                lastPage=$scope.pageMap.pageNo+2;
            }
        }else{
            $scope.firstDot=false;//前面无点
            $scope.lastDot=false;//后边无点
        }


        //构建页码
        for(var i=firstPage;i<=lastPage;i++){
            $scope.pageLabel.push(i);
        }
    }


    //分页查询
    $scope.queryByPage=function(pageNo){
        if(pageNo<1 || pageNo>$scope.resultMap.totalPages){
            return ;
        }
        $scope.pageMap.pageNo=pageNo;
        $scope.findOrder();//查询
    }

    //判断当前页是否为第一页
    $scope.isTopPage=function(){
        if($scope.pageMap.pageNo==1){
            return true;
        }else{
            return false;
        }
    }

    //判断当前页是否为最后一页
    $scope.isEndPage=function(){
        if($scope.pageMap.pageNo==$scope.resultMap.totalPages){
            return true;
        }else{
            return false;
        }
    }


});