package com.zhsx;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.zhsx.mapper")
public class ZhsxApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZhsxApplication.class, args);
    }
}
