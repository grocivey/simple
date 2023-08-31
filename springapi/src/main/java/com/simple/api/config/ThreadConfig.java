package com.simple.api.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Slf4j
@Configuration
@Order(1)
public class ThreadConfig {
    static {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> log.error("ThreadPool  {} got exception", t, e));
    }
}
