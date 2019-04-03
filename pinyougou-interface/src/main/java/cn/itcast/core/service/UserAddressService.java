package cn.itcast.core.service;

import cn.itcast.core.pojo.address.Address;
import cn.itcast.core.pojo.address.Areas;
import cn.itcast.core.pojo.address.Cities;
import cn.itcast.core.pojo.address.Provinces;
import entity.Result;

import java.util.List;

public interface UserAddressService {
    //根据当前登陆人查询用户收货地址集合
    List<Address> findAddressListByUserId(String userId);

    //查询省级分类
    List<Provinces> findProvinceList();

    //查询省级分类
    List<Cities> findByProvinceId(String provinceId);

    //查询区级分类
    List<Areas> findByCityId(String cityId);

    //新增地址
    void addAddress(Address address);

    //修改地址
    void updateAddress(Address address);

    //回显一条数据
    Address findById(Long id);

    //删除一条地址
    void delOneAddress(Long id);

    //设置默认地址
    void setDefault(Long id,String userId);
}
