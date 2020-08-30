package com.kaiqiang.learn.distributed.lock;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author kaiqiang
 * @Date 2020/08/30
 */
@SpringBootApplication
@MapperScan("com.kaiqiang.learn.distributed.lock.mysql")
public class LockApplication {

    public static void main(String[] args) {
        SpringApplication.run(LockApplication.class, args);
    }

}