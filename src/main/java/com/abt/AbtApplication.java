package com.abt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * ABT Integrated Production Manage System
 */

@SpringBootApplication
@EnableJpaAuditing
public class AbtApplication {

    public static void main(String[] args) {
        SpringApplication.run(AbtApplication.class, args);
    }

}
