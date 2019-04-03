package cn.itcast.core.controller;

import cn.itcast.core.pojo.address.Address;
import cn.itcast.core.pojo.address.Areas;
import cn.itcast.core.pojo.address.Cities;
import cn.itcast.core.pojo.address.Provinces;
import cn.itcast.core.service.UserAddressService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {

    @Reference
    private UserAddressService userAddressService;

    //根据当前登陆人查询用户收货地址集合
    @RequestMapping("/findAddressListByUserId")
    public List<Address> findAddressListByUserId() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return userAddressService.findAddressListByUserId(userId);
    }

    //查询省级分类
    @RequestMapping("/findProvinceList")
    public List<Provinces> findProvinceList() {
        return userAddressService.findProvinceList();
    }

    //查询省级分类
    @RequestMapping("/findByProvinceId")
    public List<Cities> findByProvinceId(String provinceId) {
        return userAddressService.findByProvinceId(provinceId);
    }

    //查询省级分类
    @RequestMapping("/findByCityId")
    public List<Areas> findByCityId(String cityId) {
        return userAddressService.findByCityId(cityId);
    }

    //新增地址
    @RequestMapping("/addAddress")
    public Result addAddress(@RequestBody Address address) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        address.setUserId(name);
        address.setIsDefault("0");
        try {
            userAddressService.addAddress(address);
            return new Result(true, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败");
        }
    }

    //修改地址
    @RequestMapping("/updateAddress")
    public Result updateAddress(@RequestBody Address address) {
        try {
            userAddressService.updateAddress(address);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }

    //回显数据
    @RequestMapping("/findOne")
    public Address findOne(Long id) {
        return userAddressService.findById(id);
    }

    //删除一条地址
    @RequestMapping("/delOneAddress")
    public Result delOneAddress(Long id){
        try {
            userAddressService.delOneAddress(id);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }

    //修改默认地址
    @RequestMapping("/setDefault")
    public Result setDefault(Long id) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            userAddressService.setDefault(id,userId);
            return new Result(true, "设置成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "设置失败");
        }
    }
}
