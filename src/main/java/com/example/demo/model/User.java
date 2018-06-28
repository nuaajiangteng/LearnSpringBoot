package com.example.demo.model;

import lombok.Data;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;

import java.io.Serializable;

@Data
public class User extends JdkSerializationRedisSerializer implements Serializable {
    private String userName;
    private String password;
    private Integer id;
}

