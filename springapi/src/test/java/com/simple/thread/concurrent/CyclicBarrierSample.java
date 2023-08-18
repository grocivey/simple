package com.simple.thread.concurrent;

import com.simple.api.util.threads.SimpleThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

@Slf4j
public class CyclicBarrierSample {
    @Test
    public void t1() throws InterruptedException {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(3, () -> System.out.println("已达3个"));
        for (int i = 0; i < 12; i++) {
            int finalI = i;
            SimpleThreadFactory.newThread(() -> {
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(finalI);
            }, "CyclicBarrier 测试").start();

        }

    }
}
