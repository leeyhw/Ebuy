package cn.itcast.core.service;

import cn.itcast.core.dao.address.AddressDao;
import cn.itcast.core.dao.address.AreasDao;
import cn.itcast.core.dao.address.CitiesDao;
import cn.itcast.core.dao.address.ProvincesDao;
import cn.itcast.core.pojo.address.*;
import com.alibaba.dubbo.config.annotation.Service;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class UserAddressServiceImpl implements UserAddressService {

    @Autowired
    private AddressDao addressDao;
    @Autowired
    private ProvincesDao provincesDao;
    @Autowired
    private CitiesDao citiesDao;
    @Autowired
    private AreasDao areasDao;

    //根据当前登陆人查询用户收货地址集合
    @Override
    public List<Address> findAddressListByUserId(String userId) {
        //查询地址集合
        AddressQuery addressQuery = new AddressQuery();
        addressQuery.createCriteria().andUserIdEqualTo(userId);
        List<Address> addressList = addressDao.selectByExample(addressQuery);
        if (null != addressList && addressList.size() > 0) {
            for (Address address : addressList) {
                //查询 省市区 //一个地区id 只会对应一个id且不为空 不做判断
                ProvincesQuery provincesQuery = new ProvincesQuery();
                provincesQuery.createCriteria().andProvinceidEqualTo(address.getProvinceId());
                List<Provinces> provincesList = provincesDao.selectByExample(provincesQuery);
                CitiesQuery citiesQuery = new CitiesQuery();
                citiesQuery.createCriteria().andCityidEqualTo(address.getCityId());
                List<Cities> citiesList = citiesDao.selectByExample(citiesQuery);
                AreasQuery areasQuery = new AreasQuery();
                areasQuery.createCriteria().andAreaidEqualTo(address.getTownId());
                List<Areas> areasList = areasDao.selectByExample(areasQuery);
                //拼接字符串
                String detailAddr = provincesList.get(0).getProvince() + " " + citiesList.get(0).getCity() + " " + areasList.get(0).getArea() + " " + address.getAddress();
                //设置详情地址
                address.setDetailAddr(detailAddr);
            }
        }
        return addressList;
    }

    //查询省级分类
    @Override
    public List<Provinces> findProvinceList() {
        return provincesDao.selectByExample(new ProvincesQuery());
    }

    //查询省级分类
    @Override
    public List<Cities> findByProvinceId(String provinceId) {
        CitiesQuery citiesQuery = new CitiesQuery();
        citiesQuery.createCriteria().andProvinceidEqualTo(provinceId);
        return citiesDao.selectByExample(citiesQuery);
    }

    //查询区级分类
    @Override
    public List<Areas> findByCityId(String cityId) {
        AreasQuery areasQuery = new AreasQuery();
        areasQuery.createCriteria().andCityidEqualTo(cityId);
        return areasDao.selectByExample(areasQuery);
    }

    //新增地址
    @Override
    public void addAddress(Address address) {
        addressDao.insertSelective(address);
    }

    //修改地址
    @Override
    public void updateAddress(Address address) {
        addressDao.updateByPrimaryKeySelective(address);
    }

    //回显一条数据
    @Override
    public Address findById(Long id) {
        return addressDao.selectByPrimaryKey(id);
    }

    //删除一条地址
    @Override
    public void delOneAddress(Long id) {
        addressDao.deleteByPrimaryKey(id);
    }

    //设置默认地址
    @Override
    public void setDefault(Long id,String userId) {
        //先将该用户的所有地址的默认状态修改成0
        AddressQuery addressQuery = new AddressQuery();
        addressQuery.createCriteria().andUserIdEqualTo(userId);
        List<Address> addressList = addressDao.selectByExample(addressQuery);
        if (null != addressList && addressList.size()> 0){
            for (Address address : addressList) {
                address.setIsDefault("0");
                addressDao.updateByPrimaryKeySelective(address);
            }
        }
        //修改要设置的默认地址
        Address address = new Address();
        address.setId(id);
        address.setIsDefault("1");
        addressDao.updateByPrimaryKeySelective(address);
    }

}
