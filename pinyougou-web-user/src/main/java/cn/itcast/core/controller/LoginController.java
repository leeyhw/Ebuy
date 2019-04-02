package cn.itcast.core.controller;

import cn.itcast.core.service.UserService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 登陆管理
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    @Reference
    private UserService userService;

    //根据当前登陆人查询用户昵称和头像
    @RequestMapping("/name")
    public Map<String,Object> showName(HttpServletRequest request){
        //使用SecurityContextHolder 工具类 获取用户名或是用户名对象 当前线程
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //根据当前登陆人id查询昵称与头像
        Map<String, Object> map = userService.findNickNameAndHeadPhoto(username);

        return map;
    }
}
