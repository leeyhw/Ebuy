package cn.itcast.core.service;

import java.util.Map;

public interface UserOrderService {
    //查询用户订单
    Map<String, Object> findOrder(Integer pageNum, Integer pageSize ,String name);

    //根据订单
    Map<String,String> createNative(Long orderId);

    //查询订单状态
    Map<String,String> queryPayStatus(String out_trade_no);
}
