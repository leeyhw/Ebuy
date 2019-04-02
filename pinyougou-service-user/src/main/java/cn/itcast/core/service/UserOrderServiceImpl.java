package cn.itcast.core.service;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.dao.order.OrderItemDao;
import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.dao.specification.SpecificationDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.pojo.order.OrderItemQuery;
import cn.itcast.core.pojo.order.OrderQuery;
import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.pojo.specification.Specification;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Service
public class UserOrderServiceImpl implements UserOrderService {

    @Autowired
    private OrderDao orderDao;
    @Autowired
    private SellerDao sellerDao;
    @Autowired
    private OrderItemDao orderItemDao;
    @Autowired
    private ItemDao itemDao;

    /**
     * 根据当前登陆用户id分页查询订单
     *
     * @param pageNum  当前页
     * @param pageSize 页面显示数量
     * @param name     当前登陆人
     * @return Map<String       ,               Object> :包含结果集, 总条数, 总页数
     */
    @Override
    public Map<String, Object> findOrder(Integer pageNum, Integer pageSize, String name) {
        OrderQuery orderQuery = new OrderQuery();
        OrderQuery.Criteria criteria = orderQuery.createCriteria();
        criteria.andUserIdEqualTo(name);
        //添加分页条件
        orderQuery.setOffset((pageNum - 1) * pageSize);
        orderQuery.setLimit(pageSize);
        orderQuery.setOrderByClause("create_time desc");
        //查询结果集
        List<Order> orderList = orderDao.selectByExample(orderQuery);
        for (Order order : orderList) {
            //设置商家名称
            Seller seller = sellerDao.selectByPrimaryKey(order.getSellerId());
            order.setSellerName(seller.getNickName());
            //设置订单明细
            OrderItemQuery orderItemQuery = new OrderItemQuery();
            orderItemQuery.createCriteria().andOrderIdEqualTo(order.getOrderId());
            List<OrderItem> orderItemList = orderItemDao.selectByExample(orderItemQuery);
            //添加规格
            for (OrderItem orderItem : orderItemList) {
                Item item = itemDao.selectByPrimaryKey(orderItem.getItemId());
                String spec = item.getSpec();
                String[] split = spec.replace("{", "").replace("}", "").replace("\"", "").replaceAll(":", " ").split(",");
                orderItem.setSpec(Arrays.toString(split).replace("[","").replace("]",""));
            }
            order.setOrderItemList(orderItemList);
        }

        //查询总条数 (已对sql进行优化)
        OrderQuery orderQuery1 = new OrderQuery();
        orderQuery1.createCriteria().andUserIdEqualTo(name);
        int total = orderDao.countByExample(orderQuery1);

        //创建返回结果
        Map<String, Object> resultMap = new HashMap<>();
        //总条数
        resultMap.put("total", total);
        //总页数 向上整除
        double totalPages = Math.ceil((double) total / (double) pageSize);
        resultMap.put("totalPages", (int) totalPages);
        //结果集
        resultMap.put("orderList", orderList);
        return resultMap;
    }
}
