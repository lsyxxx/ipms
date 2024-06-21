package com.abt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * ABT Integrated Production Manage System
 */

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class AbtApplication {

    public static void main(String[] args) {
        SpringApplication.run(AbtApplication.class, args);
    }

}
