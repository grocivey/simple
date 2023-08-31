package com.simple.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class App {
    public static void main(String[] args) throws InterruptedException {
        System.out.println(1001/1000);
    }
    @Test
    public void t1() throws InterruptedException, ExecutionException {
        ExecutorService service = Executors.newFixedThreadPool(11,
                new ThreadFactoryBuilder()
                        .setNameFormat("gzy-pool-%d")
                        .setUncaughtExceptionHandler((thread, throwable)-> log.error("ThreadPool {} got exception", thread,throwable))
                        .build());

        service.execute(() -> {
            log.info(String.valueOf("1"));
            System.out.println(1 / 0);
        });
        service.shutdown();
        service.awaitTermination(1, TimeUnit.HOURS);
        Thread.sleep(5000);

    }
}
