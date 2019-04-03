package cn.itcast.core.service;

import cn.itcast.core.pojo.order.OrderItem;

import java.util.List;
import java.util.Map;

public interface CollectSevice {
    //查询收藏集合
    List<OrderItem> findCollectList(String name);
}
