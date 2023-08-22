package com.simple.thread.concurrent;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class ConcurrentHashMapTest {
    //循环次数
    private static int LOOP_COUNT = 1000_0000;
    //线程数量
    private static int THREAD_COUNT = 10;
    //元素数量
    private static int ITEM_COUNT = 10;
    private Map<String, Long> normaluse() throws InterruptedException {
        ConcurrentHashMap<String, Long> freqs = new ConcurrentHashMap<>(ITEM_COUNT);
        ForkJoinPool forkJoinPool = new ForkJoinPool(THREAD_COUNT);
        forkJoinPool.execute(() -> IntStream.rangeClosed(1, LOOP_COUNT).parallel().forEach(i -> {
                    //获得一个随机的Key
                    String key = "item" + ThreadLocalRandom.current().nextInt(ITEM_COUNT);
                    synchronized (freqs) {
                        if (freqs.containsKey(key)) {
                            //Key存在则+1
                            freqs.put(key, freqs.get(key) + 1);
                        } else {
                            //Key不存在则初始化为1
                            freqs.put(key, 1L);
                        }
                    }
                }
        ));
        forkJoinPool.shutdown();
        forkJoinPool.awaitTermination(1, TimeUnit.HOURS);
        return freqs;
    }

    private Map<String, Long> gooduse() throws InterruptedException {
        ConcurrentHashMap<String, LongAdder> freqs = new ConcurrentHashMap<>(ITEM_COUNT);
        ForkJoinPool forkJoinPool = new ForkJoinPool(THREAD_COUNT);
        forkJoinPool.execute(() -> IntStream.rangeClosed(1, LOOP_COUNT).parallel().forEach(i -> {
                    String key = "item" + ThreadLocalRandom.current().nextInt(ITEM_COUNT);
                    //利用computeIfAbsent()方法来实例化LongAdder，然后利用LongAdder来进行线程安全计数
                    freqs.computeIfAbsent(key, k -> new LongAdder()).increment();
                }
        ));
        forkJoinPool.shutdown();
        forkJoinPool.awaitTermination(1, TimeUnit.HOURS);
        //因为我们的Value是LongAdder而不是Long，所以需要做一次转换才能返回
        return freqs.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey(),
                        e -> e.getValue().longValue())
                );
    }

    private Map<String, Long> gooduse2() throws InterruptedException {
        ConcurrentHashMap<String, LongAdder> freqs = new ConcurrentHashMap<>(ITEM_COUNT);
        ExecutorService forkJoinPool = Executors.newFixedThreadPool(THREAD_COUNT);
        for (int i = 0; i < LOOP_COUNT; i++) {
            forkJoinPool.execute(new Runnable() {
                @Override
                public void run() {
                    String key = "item" + ThreadLocalRandom.current().nextInt(ITEM_COUNT);
                    //利用computeIfAbsent()方法来实例化LongAdder，然后利用LongAdder来进行线程安全计数
                    freqs.computeIfAbsent(key, k -> new LongAdder()).increment();
                }
            });

        }
        forkJoinPool.shutdown();
        forkJoinPool.awaitTermination(1, TimeUnit.HOURS);
        //因为我们的Value是LongAdder而不是Long，所以需要做一次转换才能返回
        return freqs.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey(),
                        e -> e.getValue().longValue())
                );
    }

    @Test
    public void good() throws InterruptedException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("normaluse");
        System.out.println(111);
        Map<String, Long> normaluse = normaluse();
        stopWatch.stop();
        //校验元素数量
        Assert.isTrue(normaluse.size() == ITEM_COUNT, "normaluse size error");
        //校验累计总数
        Assert.isTrue(normaluse.entrySet().stream()
                        .mapToLong(item -> item.getValue()).reduce(0, Long::sum) == LOOP_COUNT
                , "normaluse count error");
        stopWatch.start("gooduse");
        System.out.println(111);
        Map<String, Long> gooduse = gooduse();
        stopWatch.stop();
        Assert.isTrue(gooduse.size() == ITEM_COUNT, "gooduse size error");
        Assert.isTrue(gooduse.entrySet().stream()
                        .mapToLong(item -> item.getValue())
                        .reduce(0, Long::sum) == LOOP_COUNT
                , "gooduse count error");
        log.info(stopWatch.prettyPrint());
    }

    public static void main(String[] args) throws InterruptedException {
//        ThreadPoolExecutor forkJoinPool = new ThreadPoolExecutor(10,10,10,TimeUnit.MINUTES,new LinkedBlockingQueue<>(),new DefaultThreadFactory("gao"));
        ForkJoinPool forkJoinPool = new ForkJoinPool(THREAD_COUNT);
        forkJoinPool.execute(()->{
            IntStream.rangeClosed(1, 1000).parallel().forEach(i -> {
                        try {
                            Thread.sleep(100000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        System.out.println("1111");
                    }
            );
        });
        Thread.sleep(3000);
        System.out.println(forkJoinPool.getActiveThreadCount());
        System.out.println(forkJoinPool.getQueuedTaskCount());
        System.out.println(forkJoinPool.getPoolSize());
//        System.out.println(forkJoinPool.getActiveCount());
//        System.out.println(forkJoinPool.getTaskCount());
//        System.out.println(forkJoinPool.getPoolSize());
    }
}
