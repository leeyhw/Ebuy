package cn.itcast.core.controller;

import cn.itcast.common.utils.FastDFSClient;
import cn.itcast.common.utils.PhoneFormatCheckUtils;
import cn.itcast.core.pojo.user.User;
import cn.itcast.core.service.UserService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Result;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 用户管理
 * 用户注册
 * 用户登陆
 * 用户个人中心
 */
@RestController
@RequestMapping("/user")
public class UserController {


    @Reference
    private UserService userService;

    //发送验证码
    @RequestMapping("/sendCode")
    public Result sendCode(String phone) {
        //判断手机号是否合法
        if (PhoneFormatCheckUtils.isPhoneLegal(phone)) {
            try {
                userService.sendCode(phone);
                return new Result(true, "发送成功");
            } catch (Exception e) {
                e.printStackTrace();
                return new Result(false, "发送失败");
            }
        } else {
            return new Result(false, "手机号不合法");
        }
    }

    //注册用户
    @RequestMapping("/add")
    public Result add(@RequestBody User user, String smscode) {

        try {
            userService.add(user, smscode);
            return new Result(true, "注册成功");
        } catch (RuntimeException e) {
            return new Result(false, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "注册失败");
        }
    }

    //修改用户信息
    @RequestMapping("/updateUserInfo")
    public Result addUserInfo(@RequestBody Map<String, String> infoMap) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        infoMap.put("name",name);
        try {
            userService.addUserInfo(infoMap);
            return new Result(true, "修改已生效");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }

    @Value("${FILE_SERVER_URL}")
    private String fsu;

    //上传图片
    @RequestMapping("/uploadFile")
    public Result uploadFile(MultipartFile file){
        try {
            //上传图片到分布式文件系统上了 FastDFS 是什么语言C写的  FastDFS的Client客户端 连接 FastDFS服务器
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:fastDFS/fdfs_client.conf");

            //扩展名
            String ext = FilenameUtils.getExtension(file.getOriginalFilename());

            //上传图片
            String path = fastDFSClient.uploadFile(file.getBytes(), ext, null);

            return new Result(true, fsu + path);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }

    }
}
