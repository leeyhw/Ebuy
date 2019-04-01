package cn.itcast.core.service;

import cn.itcast.core.pojo.user.User;

import java.util.Map;

public interface UserService {
    void sendCode(String phone);

    void add(User user, String smscode);

    //添加用户信息
    void addUserInfo(Map<String, String> infoMap);
}
