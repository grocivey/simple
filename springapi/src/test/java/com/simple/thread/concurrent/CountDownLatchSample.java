package com.simple.thread.concurrent;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchSample {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(11);
        for (int i = 0; i < 10; i++) {
            MyThreadFactory.ofNew(latch::countDown).start();
        }
        latch.await();
        System.out.println("ok");

    }

    static class MyThreadFactory {
        public static Thread ofNew(Runnable task) {
            return new Thread(task);
        }
    }
}
