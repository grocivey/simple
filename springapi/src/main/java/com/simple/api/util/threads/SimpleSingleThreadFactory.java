package com.simple.api.util.threads;


import java.util.concurrent.ThreadFactory;

/**
 * 标准简单线程工厂
 */
public enum SimpleSingleThreadFactory implements ThreadFactory {
    INSTANCE;
    private static final String POOL_NAME = "simpleSingle";

    @Override
    public Thread newThread(Runnable task) {
        Thread thread = new Thread(task);
        thread.setName(POOL_NAME + "-" + thread.getId());
        return thread;
    }

}
