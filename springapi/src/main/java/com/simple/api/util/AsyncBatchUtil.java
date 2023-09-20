package com.simple.api.util;

import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;

@Slf4j
public class AsyncBatchUtil {

    static ThreadPoolExecutor pool = new ThreadPoolExecutor(1, 20, 30, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(50000),
            new DefaultThreadFactory("AsyncBatchUtil批处理pool"),
            (r, executor) -> {
                log.error("AsyncBatchUtil线程池忙，拒绝服务");
                throw new RejectedExecutionException("服务器繁忙");
            });//拒绝策略


    public static <T> void listInertToDb(int nThreads, int meta, List<T> onlineList, Consumer<List<T>> consumer) {
        listInertToDb(false, nThreads, meta, onlineList, consumer);
    }


    @SneakyThrows
    public static <T> void listInertToDb(boolean isBlock, int nThreads, int meta, List<T> onlineList, Consumer<List<T>> consumer) {
        int onlineSize = onlineList.size();
        int groupCount = onlineSize / meta;//有几组1000个
        int overCount = onlineSize % meta;//不满一千剩下的
        int lacth = groupCount > 0 ? (overCount != 0 ? groupCount + 1 : groupCount) : 1;
        CountDownLatch countDownLatch = new CountDownLatch(lacth);
        if (groupCount > 0) {
            for (int i = 0; i < groupCount; i++) {
                List<T> tmpList = new ArrayList<>();
                for (int j = 0; j < meta; j++) {
                    tmpList.add(onlineList.get(i * meta + j));
                }
                pool.execute(() -> {
                    consumer.accept(tmpList);
                    if (isBlock) {
                        countDownLatch.countDown();
                    }
                });
            }
            if (overCount != 0) {
                List<T> tmpList = new ArrayList<>();
                for (int i = groupCount * meta; i < onlineSize; i++) {
                    tmpList.add(onlineList.get(i));
                }
                pool.execute(() -> {
                    consumer.accept(tmpList);
                    if (isBlock) {
                        countDownLatch.countDown();
                    }
                });
            }
        } else {
            pool.execute(() -> {
                consumer.accept(onlineList);
                if (isBlock) {
                    countDownLatch.countDown();
                }
            });
        }
        //是否阻塞等待执行完毕
        if (isBlock) {
            countDownLatch.await();
        }
    }


    @SneakyThrows
    public static void dbToObject(int nThreads, int meta, int allCount, Consumer<Integer> consumer) {
        int groupCount = allCount / meta;//有几组meta
        int overCount = allCount % meta;//不满剩下的
        int lacth = groupCount > 0 ? (overCount != 0 ? groupCount + 1 : groupCount) : 1;
        CountDownLatch countDownLatch = new CountDownLatch(lacth);
        if (groupCount > 0) {
            for (int i = 0; i < groupCount; i++) {
                int finalI = i;
                pool.execute(() -> {
                    consumer.accept(finalI);
                    countDownLatch.countDown();
                });
            }
            if (overCount != 0) {
                pool.execute(() -> {
                    consumer.accept(groupCount);
                    countDownLatch.countDown();
                });
            }
        } else {
            pool.execute(() -> {
                consumer.accept(0);
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
    }


    private AsyncBatchUtil() {
    }
}
