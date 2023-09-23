package com.simple.leetcode.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.util.StopWatch;

import java.util.concurrent.*;

/**
 * 1117. H2O 生成
 * 中等
 * 134
 * 相关企业
 * 现在有两种线程，氧 oxygen 和氢 hydrogen，你的目标是组织这两种线程来产生水分子。
 * <p>
 * 存在一个屏障（barrier）使得每个线程必须等候直到一个完整水分子能够被产生出来。
 * <p>
 * 氢和氧线程会被分别给予 releaseHydrogen 和 releaseOxygen 方法来允许它们突破屏障。
 * <p>
 * 这些线程应该三三成组突破屏障并能立即组合产生一个水分子。
 * <p>
 * 你必须保证产生一个水分子所需线程的结合必须发生在下一个水分子产生之前。
 * <p>
 * 换句话说:
 * <p>
 * 如果一个氧线程到达屏障时没有氢线程到达，它必须等候直到两个氢线程到达。
 * 如果一个氢线程到达屏障时没有其它线程到达，它必须等候直到一个氧线程和另一个氢线程到达。
 * 书写满足这些限制条件的氢、氧线程同步代码。
 */
class H2O5 {
    Semaphore semaphoreH = new Semaphore(2);
    Semaphore semaphoreO = new Semaphore(1);
    CyclicBarrier cyclicBarrier = new CyclicBarrier(3);

    public H2O5() {

    }

    public void hydrogen(Runnable releaseHydrogen) throws InterruptedException {
        semaphoreH.acquire();
        try {
            cyclicBarrier.await();
        } catch (BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
        releaseHydrogen.run();
        semaphoreH.release();
    }

    public void oxygen(Runnable releaseOxygen) throws InterruptedException {
        semaphoreO.acquire();
        try {
            cyclicBarrier.await();
        } catch (BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
        releaseOxygen.run();
        semaphoreO.release();

    }

    public static void main(String[] args) throws InterruptedException {
        Runnable h = () -> {
            System.out.print("H");
        };
        Runnable o = () -> {
            System.out.print("O");
        };
        H2O5 h2O = new H2O5();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ExecutorService service = Executors.newFixedThreadPool(30, new ThreadFactoryBuilder().setNameFormat("leetcode-pool-%d").build());
        int n = 10000;
        for (int i = 0; i < n; i++) {
            service.execute(() -> {
                try {
                    h2O.hydrogen(h);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            service.execute(() -> {
                try {
                    h2O.hydrogen(h);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            service.execute(() -> {
                try {
                    h2O.oxygen(o);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        service.shutdown();
        service.awaitTermination(1, TimeUnit.HOURS);
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint());
    }
}
