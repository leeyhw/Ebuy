package cn.itcast.core.service;

import cn.itcast.core.pojo.order.OrderItem;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;

@Service
public class CollectSeviceImpl implements CollectSevice {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<OrderItem> findCollectList(String name) {
        List<OrderItem> collect = (List<OrderItem>) redisTemplate.boundHashOps("Collect").get(name);

        return null;
    }
}
