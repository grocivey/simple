package com.simple.api.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Order(1)
@Component
public class ThreadConfig {
    static {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> log.error("ThreadPool  {} got exception", t, e));
    }
}
