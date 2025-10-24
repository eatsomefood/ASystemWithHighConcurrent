package com.star.highconcurrent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("com.star.highconcurrent.*")
@SpringBootApplication
public class HighConcurrentApplication {

    public static void main(String[] args) {
        SpringApplication.run(HighConcurrentApplication.class, args);
    }

}
