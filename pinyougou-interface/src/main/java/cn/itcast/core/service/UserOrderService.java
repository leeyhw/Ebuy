package cn.itcast.core.service;

import java.util.Map;

public interface UserOrderService {
    //查询用户订单
    Map<String, Object> findOrder(Integer pageNum, Integer pageSize ,String name);
}
