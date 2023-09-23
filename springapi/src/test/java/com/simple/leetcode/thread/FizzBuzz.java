package com.simple.leetcode.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.util.StopWatch;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.IntConsumer;

/**
 * 1195. 交替打印字符串
 * 中等
 * 95
 * 相关企业
 * 编写一个可以从 1 到 n 输出代表这个数字的字符串的程序，但是：
 * <p>
 * 如果这个数字可以被 3 整除，输出 "fizz"。
 * 如果这个数字可以被 5 整除，输出 "buzz"。
 * 如果这个数字可以同时被 3 和 5 整除，输出 "fizzbuzz"。
 * 例如，当 n = 15，输出： 1, 2, fizz, 4, buzz, fizz, 7, 8, fizz, buzz, 11, fizz, 13, 14, fizzbuzz。
 * <p>
 * 假设有这么一个类：
 * <p>
 * class FizzBuzz {
 *   public FizzBuzz(int n) { ... }               // constructor
 *   public void fizz(printFizz) { ... }          // only output "fizz"
 *   public void buzz(printBuzz) { ... }          // only output "buzz"
 *   public void fizzbuzz(printFizzBuzz) { ... }  // only output "fizzbuzz"
 *   public void number(printNumber) { ... }      // only output the numbers
 * }
 * 请你实现一个有四个线程的多线程版  FizzBuzz， 同一个 FizzBuzz 实例会被如下四个线程使用：
 * <p>
 * 线程A将调用 fizz() 来判断是否能被 3 整除，如果可以，则输出 fizz。
 * 线程B将调用 buzz() 来判断是否能被 5 整除，如果可以，则输出 buzz。
 * 线程C将调用 fizzbuzz() 来判断是否同时能被 3 和 5 整除，如果可以，则输出 fizzbuzz。
 * 线程D将调用 number() 来实现输出既不能被 3 整除也不能被 5 整除的数字。
 * <p>
 *
 * 提示：
 * <p>
 * 本题已经提供了打印字符串的相关方法，如 printFizz() 等，具体方法名请参考答题模板中的注释部分。
 */
class FizzBuzz {
    private int n;
    private int base = 1;
    private Semaphore fizz = new Semaphore(0);
    private Semaphore buzz = new Semaphore(0);
    private Semaphore fizzbuzz = new Semaphore(0);
    private Semaphore number = new Semaphore(0);

    public FizzBuzz(int n) {
        this.n = n;
    }

    // printFizz.run() outputs "fizz".
    public void fizz(Runnable printFizz) throws InterruptedException {
        while (base <= n) {
            if (fizz.tryAcquire()){
                printFizz.run();
                number.release();
            }
           Thread.yield();
        }


    }

    // printBuzz.run() outputs "buzz".
    public void buzz(Runnable printBuzz) throws InterruptedException {
        while (base <= n) {
            if (buzz.tryAcquire()) {
                printBuzz.run();
                number.release();
            }
            Thread.yield();

        }
    }

    // printFizzBuzz.run() outputs "fizzbuzz".
    public void fizzbuzz(Runnable printFizzBuzz) throws InterruptedException {
        while (base <= n) {
            if (fizzbuzz.tryAcquire()){
                printFizzBuzz.run();
                number.release();
            }
            Thread.yield();

        }
    }

    // printNumber.accept(x) outputs "x", where x is an integer.
    public void number(IntConsumer printNumber) throws InterruptedException {
        for (; base <= n ; base++){
//            System.out.println("===="+base);
            if (base % 3 == 0 && base % 5 != 0){
                fizz.release();
                number.acquire();
            }else if (base % 5 == 0 && base % 3 != 0){
                buzz.release();
                number.acquire();
            }else if (base % 15 == 0){
                fizzbuzz.release();
                number.acquire();
            }else {
                printNumber.accept(base);
            }
        }
    }


    public static void main(String[] args) throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(4, new ThreadFactoryBuilder().setNameFormat("leetcode-pool-%d").build());
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        FizzBuzz fizzBuzz = new FizzBuzz(100000);
        service.execute(() -> {
            try {
                fizzBuzz.fizz(()-> System.out.println("fizz"));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        service.execute(() -> {
            try {
                fizzBuzz.buzz(()-> System.out.println("buzz"));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        service.execute(() -> {
            try {
                fizzBuzz.fizzbuzz(()-> System.out.println("fizzbuzz"));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        service.execute(() -> {
            try {
                fizzBuzz.number(System.out::println);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        service.shutdown();
        service.awaitTermination(1, TimeUnit.HOURS);
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint());




//        SimpleThreadFactory.newThread(() -> {
//            try {
//                fizzBuzz.fizz(()-> System.out.println("fizz"));
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        },"fizz").start();
//        SimpleThreadFactory.newThread(() -> {
//            try {
//                fizzBuzz.buzz(()-> System.out.println("buzz"));
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        },"buzz").start();
//        SimpleThreadFactory.newThread(() -> {
//            try {
//                fizzBuzz.fizzbuzz(()-> System.out.println("fizzbuzz"));
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        },"fizzbuzz").start();
//        SimpleThreadFactory.newThread(() -> {
//            try {
//                fizzBuzz.number(System.out::println);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        },"number").start();
        System.out.println("=============");
    }
}
