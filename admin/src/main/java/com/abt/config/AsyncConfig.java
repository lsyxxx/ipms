package com.abt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 配置线程池
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "abtThreadPool")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // 核心线程数
        executor.setMaxPoolSize(10); // 最大线程数
        executor.setQueueCapacity(100); // 任务队列容量
        executor.setThreadNamePrefix("ABT-Async-Thread-"); // 线程名前缀
        //当线程池满时，将任务退回到原有线程中同步执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); // 拒绝策略
        executor.initialize();
        return executor;
    }

}
