package com.simple.leetcode.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.util.StopWatch;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.IntConsumer;

/**
 * 1116. 打印零与奇偶数
 * 中等
 * 150
 * 相关企业
 * 现有函数 printNumber 可以用一个整数参数调用，并输出该整数到控制台。
 * <p>
 * 例如，调用 printNumber(7) 将会输出 7 到控制台。
 * 给你类 ZeroEvenOdd 的一个实例，该类中有三个函数：zero、even 和 odd 。ZeroEvenOdd 的相同实例将会传递给三个不同线程：
 * <p>
 * 线程 A：调用 zero() ，只输出 0
 * 线程 B：调用 even() ，只输出偶数
 * 线程 C：调用 odd() ，只输出奇数
 * 修改给出的类，以输出序列 "010203040506..." ，其中序列的长度必须为 2n 。
 * <p>
 * 实现 ZeroEvenOdd 类：
 * <p>
 * ZeroEvenOdd(int n) 用数字 n 初始化对象，表示需要输出的数。
 * void zero(printNumber) 调用 printNumber 以输出一个 0 。
 * void even(printNumber) 调用printNumber 以输出偶数。
 * void odd(printNumber) 调用 printNumber 以输出奇数。
 * <p>
 *
 * 示例 1：
 * <p>
 * 输入：n = 2
 * 输出："0102"
 * 解释：三条线程异步执行，其中一个调用 zero()，另一个线程调用 even()，最后一个线程调用odd()。正确的输出为 "0102"。
 * 示例 2：
 * <p>
 * 输入：n = 5
 * 输出："0102030405"
 * <p>
 *
 * 提示：
 * <p>
 * 1 <= n <= 1000
 */
class ZeroEvenOdd2 {
    private int n;
    private volatile int ns = 0;
    private final Semaphore semaphorZero = new Semaphore(1);
    private final Semaphore semaphorEven = new Semaphore(0);
    private final Semaphore semaphorOdd = new Semaphore(0);

    public ZeroEvenOdd2(int n) {
        this.n = n;
    }

    // printNumber.accept(x) outputs "x", where x is an integer.
    public void zero(IntConsumer printNumber) throws InterruptedException {
        while (ns <= n){
            semaphorZero.acquire();
            if (ns == n){
                ns++;
                semaphorOdd.release();
                semaphorEven.release();
                break;
            }
            printNumber.accept(0);
            if (ns%2==0){
                ns++;
                semaphorEven.release();
            }else {
                ns++;
                semaphorOdd.release();
            }


        }
    }

    public void even(IntConsumer printNumber) throws InterruptedException {
        while (ns <= n){
            semaphorEven.acquire();
            if (ns > n){
                break;
            }
            printNumber.accept(ns);
            semaphorZero.release();
        }
    }

    public void odd(IntConsumer printNumber) throws InterruptedException {
        while (ns <= n){
            semaphorOdd.acquire();
            if (ns > n){
                break;
            }
            printNumber.accept(ns);
            semaphorZero.release();
        }
    }

    public static class Task1 implements Runnable{
        ZeroEvenOdd zeroEvenOdd;

        public Task1(ZeroEvenOdd zeroEvenOdd) {
            this.zeroEvenOdd = zeroEvenOdd;
        }


        @Override
        public void run() {
            IntConsumer consumer = System.out::print;
            try {
                zeroEvenOdd.zero(consumer);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class Task2 implements Runnable{
        ZeroEvenOdd zeroEvenOdd;

        public Task2(ZeroEvenOdd zeroEvenOdd) {
            this.zeroEvenOdd = zeroEvenOdd;
        }


        @Override
        public void run() {
            IntConsumer consumer = System.out::print;
            try {
                zeroEvenOdd.even(consumer);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class Task3 implements Runnable{
        ZeroEvenOdd zeroEvenOdd;

        public Task3(ZeroEvenOdd zeroEvenOdd) {
            this.zeroEvenOdd = zeroEvenOdd;
        }


        @Override
        public void run() {
            IntConsumer consumer = System.out::print;
            try {
                zeroEvenOdd.odd(consumer);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public static void main(String[] args) throws InterruptedException {


        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ZeroEvenOdd zeroEvenOdd = new ZeroEvenOdd(9);
        ExecutorService service = Executors.newFixedThreadPool(3, new ThreadFactoryBuilder().setNameFormat("leetcode-pool-%d").build());
        service.execute(new Task1(zeroEvenOdd));
        service.execute(new Task2(zeroEvenOdd));
        service.execute(new Task3(zeroEvenOdd));
        service.shutdown();
        service.awaitTermination(1, TimeUnit.HOURS);
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint());

    }
}
