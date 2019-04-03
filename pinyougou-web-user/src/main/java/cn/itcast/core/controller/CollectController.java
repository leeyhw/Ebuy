package cn.itcast.core.controller;

import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.service.CollectSevice;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/collect")
public class CollectController {

    @Reference
    private CollectSevice collectSevice;

    //查询收藏集合
    @RequestMapping("/findCollectList")
    public List<OrderItem> findCollectList() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return collectSevice.findCollectList(name);
    }
}
