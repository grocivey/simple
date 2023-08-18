package com.simple.thread.concurrent;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class ListConcurrentSample {
    @Test
    public void t1() {
        long startCAW = System.currentTimeMillis();
        List<Integer> strings = new ArrayList<>();
        ExecutorService service = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 100000; i++) {
            int finalI = i;
            service.execute(() -> strings.add(finalI));
        }
        service.shutdown();
        while (!service.isTerminated()) {
        }
        long endCAW = System.currentTimeMillis();
        log.info("list.size:{}", strings.size());
        log.info("耗时:{}", endCAW - startCAW);
    }

    @Test
    public void t2() {
        long startCAW = System.currentTimeMillis();
        List<Integer> strings = new CopyOnWriteArrayList<>();
        ExecutorService service = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 100000; i++) {
            int finalI = i;
            service.execute(() -> strings.add(finalI));
        }
        service.shutdown();
        while (!service.isTerminated()) {
        }
        long endCAW = System.currentTimeMillis();
        log.info("list.size:{}", strings.size());
        log.info("耗时:{}", endCAW - startCAW);
    }

    @Test
    public void t3() {
        long startCAW = System.currentTimeMillis();
        ConcurrentLinkedQueue<Integer> objects = new ConcurrentLinkedQueue<>();
        ExecutorService service = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 100000; i++) {
            int finalI = i;
            service.execute(() -> objects.add(finalI));
        }
        service.shutdown();
        while (!service.isTerminated()) {
        }
        List<Integer> list = new ArrayList<>(objects);
        long endCAW = System.currentTimeMillis();
        log.info("list.size:{}", list.size());
        log.info("耗时:{}", endCAW - startCAW);
    }
}
