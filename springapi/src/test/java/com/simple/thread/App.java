package com.simple.thread;

import com.simple.api.util.threads.SimpleSingleThreadFactory;
import com.simple.api.util.threads.SimpleThreadFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {
    public static void main(String[] args) throws InterruptedException {
        SimpleSingleThreadFactory.INSTANCE.newThread(()-> log.info("Hello world!")).start();
        Thread.sleep(300);
        SimpleThreadFactory.newThread(()->log.info("Hello world!")).start();
        Thread.sleep(300);
        SimpleThreadFactory.newThread(()->log.info("Hello world!"), "嘻嘻").start();
        Thread.sleep(300);
        SimpleThreadFactory.FACTORY.newThread(()->log.info("Hello world!")).start();
    }
}
