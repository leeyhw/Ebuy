app.controller('addressController', function ($scope, $controller, addressService) {
    $controller('baseController', {$scope: $scope});

    $scope.entity={};
    //查询用户的地址列表
    $scope.findAdressList = function () {
        addressService.findAdressList().success(
            function (response) {
                $scope.addressList = response;
            }
        )
    };

    //设置地址别名
    $scope.setAlias = function (alias) {
        $scope.entity.alias = alias;
    };

    //保存地址
    $scope.save =function () {
        //常规判断
         if (null == $scope.entity.contact || $scope.entity.contact.trim()==''){
             alert("请填写收货人");
             return;
         }
         if (null == $scope.entity.cityId || null == $scope.entity.provinceId || null == $scope.entity.townId){
             alert("请选择所在地区");
             return;
         }
         if (null == $scope.entity.address || $scope.entity.contact.trim()==''){
             alert("请填写详细地址");
             return;
         }
         if (null == $scope.entity.mobile || $scope.entity.mobile.trim()==''){
             alert("请填写联系电话");
             return;
         }
         //进行判断是新增还是修改
         var object;
         if ($scope.entity.id == null){
             object = addressService.addAddress($scope.entity);
         }else {
             object = addressService.updateAddress($scope.entity);
         }
         object.success(
             function (response) {
                 if (response.flag){
                     alert(response.message)
                     location.reload();
                 }else {
                     alert(response.message);
                 }
             }
         )
    };

    // 查询省级分类列表
    $scope.selectProvinceList = function(){
        addressService.findProvinceList(0).success(function(response){//List<Province>
            $scope.provinceList = response;//List<Province>
        });
    };

    // 查询市级分类列表:
    $scope.$watch("entity.provinceId",function(newValue,oldValue){
        addressService.findByProvinceId(newValue).success(function(response){
            $scope.cityList = response;
            $scope.townList = null;
        });
    });

    // 查询区级分类列表:
    $scope.$watch("entity.cityId",function(newValue,oldValue){
        addressService.findByCityId(newValue).success(function(response){
            $scope.townList = response;
        });
    });

    //回显
    $scope.findOne = function(id){
        addressService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        )
    };

    //删除一条地址
    $scope.delOneAddress = function (id,isDefault) {
        if (isDefault == '1'){
            alert("该地址为默认地址，请先修改默认地址");
            return;
        }
        if (confirm("您确定要删除该地址吗?")){
            addressService.delOneAddress(id).success(
                function (response) {
                    if (response.flag){
                        alert(response.message);
                        location.reload();
                    }else {
                        alert(response.message)
                    }
                }
            )
        }
    };

    //设置默认地址
    $scope.setDefault = function (id) {
        addressService.setDefault(id).success(
            function (response) {
                if (response.flag){
                    alert(response.message);
                    location.reload();
                }else {
                    alert(response.message);
                }
            }
        )
    }



});