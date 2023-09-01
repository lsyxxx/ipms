package com.abt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Integrated Production Manage System
 */

@SpringBootApplication
@EnableJpaAuditing
//@EnableJpaRepositories(basePackages = "com.abt.flow.repository")
public class AbtApplication {

    public static void main(String[] args) {
        SpringApplication.run(AbtApplication.class, args);
    }

}
