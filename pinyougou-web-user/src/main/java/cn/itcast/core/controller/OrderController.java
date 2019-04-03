package cn.itcast.core.controller;

import cn.itcast.core.service.OrderService;
import cn.itcast.core.service.UserOrderService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Result;
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

    //根据订单
    @RequestMapping("/createNative")
    public Map<String,String> createNative(Long orderId){
        return userOrderService.createNative(orderId);
    }

    //查询订单状态
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
        try {
            int x = 0;
            //无限循环
            while (true){
                Map<String,String> map = userOrderService.queryPayStatus(out_trade_no);
                if("NOTPAY".equals(map.get("trade_state"))){
                    //休息一会
                    Thread.sleep(5000);
                    x++;
                    if(x >= 60){
                        return new Result(false,"支付超时");
                    }
                }else{
                    //收尾：改状态
                    //map : 支付成功之后流水号
                    //支付成功的时间
                    return new Result(true,"支付成功");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"支付失败");
        }
    }
}
