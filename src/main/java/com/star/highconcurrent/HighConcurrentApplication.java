package com.star.highconcurrent;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@MapperScan("com.star.highconcurrent.mapper")
@SpringBootApplication
public class HighConcurrentApplication {

    public static void main(String[] args) {
        SpringApplication.run(HighConcurrentApplication.class, args);
    }

}
