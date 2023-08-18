package com.simple.thread.factory;

import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadFactory;

/**
 * 创建线程工厂类
 *
 * @author gro
 */
@Slf4j
public final class SimpleThreadFactory {
    public static final ThreadFactory FACTORY = new DefaultThreadFactory("Gro");
    private SimpleThreadFactory() {
    }

    public static Thread newThread(Runnable r) {
        return FACTORY.newThread(r);
    }

    public static Thread newThread(Runnable r, String threadName) {
        Thread thread = newThread(r);
        thread.setName(thread.getName() + "-" + threadName);
        return thread;
    }


}
