package com.abt.wxapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.abt")
@EntityScan(basePackages = "com.abt")
@EnableJpaRepositories(basePackages = "com.abt")
@EnableJpaAuditing
@EnableScheduling
@EnableAsync
@EntityScan(basePackages = {"com.abt.wxapp", "com.abt.chkmodule.entity"})
public class WxappApplication {

    public static void main(String[] args) {
        SpringApplication.run(WxappApplication.class, args);
    }

}
