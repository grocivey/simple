package com.simple.leetcode.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.util.StopWatch;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlockQueue<T> {

    LinkedList<T> list = new LinkedList<>();
    final int capacity;
    Lock lockPut = new ReentrantLock();
    Lock lockTake = new ReentrantLock();
    Condition notFull = lockPut.newCondition();
    Condition notEmpty = lockPut.newCondition();
    volatile int nowSize = 0;

    public BlockQueue(int capacity) {
        this.capacity = capacity;
    }

    public boolean put(T t) throws InterruptedException {
        int c = -1;
        lockPut.lock();
        try {
            while (nowSize == capacity) {
                notFull.await();
            }
            list.add(t);
            nowSize++;
            if (nowSize < capacity){
                notFull.signalAll();
            }
            notEmpty.signal();

        } finally {
            lockPut.unlock();
        }

        return true;
    }
    public T take() throws InterruptedException {
        int c = -1;
        T r;
        lockPut.lock();
        try {
            while (nowSize == 0) {
                notEmpty.await();
            }
            r = list.removeFirst();
            nowSize--;
            if (nowSize > 0){
                notEmpty.signalAll();
            }
            notFull.signal();
            return r;
        } finally {
            lockPut.unlock();
        }

    }

    public static void main(String[] args) throws InterruptedException {

        AtomicInteger atomicInteger = new AtomicInteger();
        ExecutorService service = Executors.newFixedThreadPool(10, new ThreadFactoryBuilder().setNameFormat("leetcode-pool-%d").build());
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("queue");
        BlockQueue<Object> blockQueue = new BlockQueue<>(100);
//        LinkedBlockingQueue<Object> blockQueue = new LinkedBlockingQueue<>(100);
        for (int i = 0; i < 5; i++) {
            service.execute(()->{
                for (int j = 0; j < 1000000; j++) {
                    try {
                        blockQueue.put(1);
                        atomicInteger.incrementAndGet();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            service.execute(()->{
                for (int j = 0; j < 1000000; j++) {
                    try {
                        blockQueue.take();
//                        System.out.println();
                        atomicInteger.incrementAndGet();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
        service.shutdown();
        service.awaitTermination(1, TimeUnit.HOURS);
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint());
        System.out.println("total:" + atomicInteger.get());
    }

}
