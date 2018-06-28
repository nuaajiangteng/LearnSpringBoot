package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.JwtUtil;

import com.example.demo.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Component
@RestController
@RequestMapping("/api")
@Api(value = "/user-controller", description = "用户操作接口")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    private ValueOperations<String, Object> valueOperations;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

//    @Autowired
//    private getFieldValueByName getUserName;

    @Value("${com.neo.title}")
    private String title;

    @ApiOperation(value="获取用户信息", notes="获取用户信息")
    @RequestMapping(value="login", method = RequestMethod.POST)
    public Map<String,Object> login(@RequestBody User user){
        Map<String,Object> map = new HashMap<>();
        List<User> list = userService.getUserList(user);
        List<User> emptyArray = new ArrayList<>();
        if (list.size() == 1) {
            // 登陆成功返回token
            String jwt = JwtUtil.generateToken(user.getUserName());
            map.put("success", true);
            map.put("data", list);
            map.put("token", jwt);

            // Redis键值对(用户名, token)
            this.valueOperations.set(user.getUserName(), jwt);

        } else {
            map.put("success", false);
            map.put("data", emptyArray);
            map.put("errorMessage", "用户登录失败");
            map.put("title", title);
        }
        return map;

    }

    @ApiOperation(value="文件上传", notes="文件上传")
    @RequestMapping(value = "upload",  method = RequestMethod.POST)
    public String upload(@RequestParam(value = "file", required = false) MultipartFile file, HttpServletRequest request, @RequestBody User user) {

        String token = request.getHeader("Authorization");

        // 判断key是否存在
        Boolean hasKey = this.redisTemplate.hasKey(user.getUserName());
        assertEquals(true, hasKey);

        // 获取Redis存储的token
        Object str = this.valueOperations.get(user.getUserName());
        assertNotNull(str);

        if (!token.equals(str)) {
            return "token已过期或无效";
        }

        if (file == null) {
            return "文件为空";
        }
        // 获取文件名
        String fileName = file.getOriginalFilename();
        // 获取文件的后缀名
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        // 文件上传路径
        String filePath = "/Users/jiangteng/IdeaProjects/springBoot/src/main/resources/static/";
        // 解决中文问题,liunx 下中文路径,图片显示问题
        //fileName = UUID.randomUUID() + suffixName;
        File dest = new File(filePath + fileName);
        // 检测是否存在目录
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        try {
            file.transferTo(dest);
            return "上传成功";
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "上传失败";
    }

}



