package cn.itcast.core.service;

import cn.itcast.core.pojo.user.User;

import java.util.Map;

public interface UserService {
    void sendCode(String phone);

    void add(User user, String smscode);

    //修改用户信息
    void addUserInfo(Map<String, String> infoMap);

    //根据当前登陆人查询头像和昵称
    Map<String,Object> findNickNameAndHeadPhoto(String username);
}
