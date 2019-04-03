package cn.itcast.core.listener;

import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.service.StaticPageService;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * 消息 处理类  自定义的
 */
public class PageListener implements MessageListener {


    @Autowired
    private StaticPageService staticPageService;
    @Autowired
    private GoodsDao goodsDao;
    //接收的消息的方法
    @Override
    public void onMessage(Message message) {
        ActiveMQTextMessage atm = (ActiveMQTextMessage) message;

        try {
            String id = atm.getText();
            Goods goods = goodsDao.selectByPrimaryKey(Long.getLong(id));



            if(goods.getIsMarketable().equals("1")) {
                System.out.println("静态化项目接收到的Id:" + id);


                //3:静态化  模板 + 数据 == 输出
                staticPageService.index(Long.parseLong(id));
            }
            else{

            }

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
