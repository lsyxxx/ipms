package com.ipms;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Integrated Production Manage System
 */

@SpringBootApplication
@EnableKnife4j
public class IpmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(IpmsApplication.class, args);
    }

}
