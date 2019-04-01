package cn.itcast.core.service;

import cn.itcast.core.dao.user.UserDao;
import cn.itcast.core.pojo.user.User;
import cn.itcast.core.pojo.user.UserQuery;
import com.alibaba.dubbo.config.annotation.Service;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 用户管理
 */
@Service
@Transactional
public class UserServiceImpl implements  UserService {


    //发消息 Map
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private Destination smsDestination;
    @Autowired
    private UserDao userDao;

    @Override
    public void sendCode(final String phone) {
        //1:生成验证码
        final String randomNumeric = RandomStringUtils.randomNumeric(6);
        //2:保存验证码到缓存中
        redisTemplate.boundValueOps(phone).set(randomNumeric);
        //redisTemplate.boundValueOps(phone).expire(1, TimeUnit.MINUTES);
        //2:发消息 Map
        jmsTemplate.send(smsDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {

                MapMessage map = session.createMapMessage();
                //手机号
                map.setString("iphone",phone);//"17801040609"
                //验证码
                map.setString("templateParam","{'number':'"+randomNumeric+"'}");
                //签名
                map.setString("signName","品优购商城");
                //模板ID
                map.setString("templateCode","SMS_126462276");


                return map;
            }
        });
    }

    //添加
    @Override
    public void add(User user, String smscode) {
        String code = (String) redisTemplate.boundValueOps(user.getPhone()).get();
        //判断验证码是否失效
        if(null == code){
            throw new RuntimeException("验证码失败");
        }

        if(code.equals(smscode)){
            //保存用户信息
            user.setCreated(new Date());
            user.setUpdated(new Date());
            //密码加密
            userDao.insertSelective(user);
        }else{
            throw new RuntimeException("验证码不正确");
        }
    }


    //添加用户信息
    @Override
    public void addUserInfo(Map<String, String> infoMap) {
        User user = new User();
        if (null != infoMap.get("name") && !"".equals(infoMap.get("name").trim())) {
            user.setUsername(infoMap.get("name"));
        }

        UserQuery userQuery = new UserQuery();
        userQuery.createCriteria().andUsernameEqualTo(user.getUsername());
        List<User> users = userDao.selectByExample(userQuery);

        //设置昵称
        if (null != infoMap.get("nickName") && !"".equals(infoMap.get("nickName").trim())) {
            user.setNickName(infoMap.get("nickName"));
        }
        //设置性别
        if (!"0".equals(infoMap.get("gender"))) {
            user.setSex(infoMap.get("gender"));
        }
        //设置生日
        if (null != infoMap.get("birthday") && !"".equals(infoMap.get("birthday").trim())) {
            try {
                Date birthday = new SimpleDateFormat("yyyy-MM-dd").parse(infoMap.get("birthday"));
                user.setBirthday(birthday);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        //设置地址
        if (null != infoMap.get("address") && !"".equals(infoMap.get("address").trim())) {
            user.setAddress(infoMap.get("address"));
        }
        //设置工作
        if (null != infoMap.get("job") && !"".equals(infoMap.get("job").trim())) {
            user.setOccupation(infoMap.get("job"));
        }
        //设置头像路径
        if (null != infoMap.get("picPath") && !"".equals(infoMap.get("picPath").trim())) {
            user.setHeadPic(infoMap.get("picPath"));
        }
        if (users.size()>0){
            user.setId(users.get(0).getId());
            userDao.updateByPrimaryKeySelective(user);
        }else {
            throw new RuntimeException("找不到用户id");
        }

    }
}
