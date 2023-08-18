package com.simple.thread.concurrent;

import com.simple.api.util.threads.SimpleThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

@Slf4j
public class CountDownLatchSample {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            SimpleThreadFactory.newThread(()->{
                latch.countDown();
                log.info("latch.countDown()");
            }, "CountDownLatch测试").start();
        }
        latch.await();
        System.out.println("ok");

    }


}
