package cn.itcast.core.controller;

import cn.itcast.core.service.OrderService;
import cn.itcast.core.service.UserOrderService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference
    private UserOrderService userOrderService;

    //查询当前用户的订单数
    @RequestMapping("/findOrder")
    public Map<String, Object> findOrder(Integer page, Integer rows) {
        //获取当前登陆人
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return userOrderService.findOrder(page, rows, name);
    }
}
