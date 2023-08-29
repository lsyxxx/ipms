package com.abt;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Integrated Production Manage System
 */

@SpringBootApplication
@MapperScan("com.abt.flow.dao")
public class AbtApplication {

    public static void main(String[] args) {
        SpringApplication.run(AbtApplication.class, args);
    }

}
