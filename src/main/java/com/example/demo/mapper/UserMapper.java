package com.example.demo.mapper;

import com.example.demo.model.User;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public interface UserMapper {

    @Select("SELECT * FROM USER where userName=#{userName} and password=#{password}")
    List<User> getUserList(User user);

}

