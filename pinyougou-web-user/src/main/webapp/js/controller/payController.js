app.controller('payController' ,function($scope ,$location,payService){
	

	//生成二维码
	$scope.createNative=function(){
		$scope.out_trade_no = $location.search()['orderId'];
		payService.createNative($scope.out_trade_no).success(
			function(response){ // 返回值：Map  K code_url V:url  微信地址 微信服务器给 weixi:// 自定义协议

				//订单ID
				//金额
				//二维码 value值

				//显示订单号和金额
				$scope.money= (response.total_fee*100/100).toFixed(2);

				//生成二维码
				 var qr=new QRious({
					    element:document.getElementById('qrious'),
						size:250,
						value:response.code_url,
						level:'H'
			     });



                queryPayStatus();//调用查询
				
			}	
		);	
	}
	
	//调用查询
	queryPayStatus=function(){
		payService.queryPayStatus($scope.out_trade_no).success(
			function(response){
				if(response.flag){
					location.href="paysuccess.html#?money="+$scope.money;
				}else{
					if(response.message=='二维码超时'){
						$scope.createNative();//重新生成二维码
					}else{
						location.href="payfail.html";
					}
				}				
			}		
		);		
	}
	
	//获取金额
	$scope.getMoney=function(){
		return $location.search()['money'];
	}
	
});