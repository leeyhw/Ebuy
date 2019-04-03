package cn.itcast.core.service;

import cn.itcast.common.utils.HttpClient;
import cn.itcast.common.utils.IdWorker;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.log.PayLogDao;
import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.dao.order.OrderItemDao;
import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.dao.specification.SpecificationDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.log.PayLog;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.pojo.order.OrderItemQuery;
import cn.itcast.core.pojo.order.OrderQuery;
import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.pojo.specification.Specification;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.*;

@Service
public class UserOrderServiceImpl implements UserOrderService {

    //appid： 微信公众账号或开放平台APP的唯一标识
    @Value("${appid}")
    private String appid;

    //partner：财付通平台的商户账号
    @Value("${partner}")
    private String partner;

    //partnerkey：财付通平台的商户密钥
    @Value("${partnerkey}")
    private String partnerkey;

    @Autowired
    private OrderDao orderDao;
    @Autowired
    private PayLogDao payLogDao;
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
     * @return Map<String ,  Object> :包含结果集, 总条数, 总页数
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
            //添加字符串格式的id
            order.setOrderIdStr(String.valueOf(order.getOrderId()));
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

    //根据订单
    @Override
    public Map<String, String> createNative(Long orderId) {
        Order order = orderDao.selectByPrimaryKey(orderId);
        //1. 生成参数
        Map<String, String> param = new HashMap<>();
        //公众账号id
        param.put("appid", appid);
        //商户号
        param.put("mch_id", partner);
        //随机字符串
        param.put("nonce_str", WXPayUtil.generateNonceStr());
        //商品描述
        param.put("body", "品优购商城");
        //商户订单号
        param.put("out_trade_no", String.valueOf(orderId));
        //标价金额
//        param.put("total_fee", String.valueOf(order.getPayment()));
        param.put("total_fee", "1");
        //终端IP
        param.put("spbill_create_ip", "127.0.0.1");
        //通知地址
        param.put("notify_url", "http://www.itcast.cn");
        //交易类型
        param.put("trade_type", "NATIVE");

        try {
            //2. 创建要发送的xml 和签名
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setXmlParam(xmlParam);
            httpClient.setHttps(true);
            httpClient.post();

            //3. 获取结果
            String resultXml = httpClient.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(resultXml);
            HashMap<String, String> map = new HashMap<>();
            //返回的显示金额
            map.put("total_fee", String.valueOf(order.getPayment()));
            //生成的二维码地址
            map.put("code_url", resultMap.get("code_url"));
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //查询订单状态
    @Override
    public Map<String, String> queryPayStatus(String out_trade_no) {
        //1. 创建参数
        Map<String, String> param = new HashMap<>();
        //公众账号ID
        param.put("appid", appid);
        //商户号
        param.put("mch_id", partner);
        //微信订单号 和 商户订单号 我们可以二选一
        param.put("out_trade_no", out_trade_no);
        //随机字符串
        param.put("nonce_str", WXPayUtil.generateNonceStr());
        try {
            //2. 创建要发送xml 和签名
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setXmlParam(xmlParam);
            httpClient.setHttps(true);
            httpClient.post();
            //3. 返回结果
            String xmlResult = httpClient.getContent();
            Map<String, String> mapResult = WXPayUtil.xmlToMap(xmlResult);
            if ("SUCCESS".equals(mapResult.get("trade_state"))){
                //1. 修改日志表状态
                //2. 修改订单关联表状态
                //3. 清空日志缓存
                updateOrderStatus(mapResult.get("transaction_id"),out_trade_no);
            }
            return mapResult;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //修改支付状态
    public void updateOrderStatus(String transaction_id,String out_trade_no) {
        //修改订单关联表
        Order order = new Order();
        order.setStatus("2");
        //付款时间
        order.setPaymentTime(new Date());
        //更新时间
        order.setUpdateTime(new Date());
        order.setOrderId(Long.parseLong(out_trade_no.trim()));
        orderDao.updateByPrimaryKeySelective(order);
        //修改日志状态
        PayLog payLog = new PayLog();
        payLog.setPayTime(new Date());
        //流水号
        payLog.setTransactionId(transaction_id);
        //付款状态
        payLog.setTradeState("1");
        payLogDao.updateByPrimaryKeySelective(payLog);

    }
}
