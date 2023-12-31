package com.simple.leetcode.thread;

/**
 * 1114. 按序打印
 * 简单
 * 483
 * 相关企业
 * 给你一个类：
 * <p>
 * public class Foo {
 * public void first() { print("first"); }
 * public void second() { print("second"); }
 * public void third() { print("third"); }
 * }
 * 三个不同的线程 A、B、C 将会共用一个 Foo 实例。
 * <p>
 * 线程 A 将会调用 first() 方法
 * 线程 B 将会调用 second() 方法
 * 线程 C 将会调用 third() 方法
 * 请设计修改程序，以确保 second() 方法在 first() 方法之后被执行，third() 方法在 second() 方法之后被执行。
 * <p>
 * 提示：
 * <p>
 * 尽管输入中的数字似乎暗示了顺序，但是我们并不保证线程在操作系统中的调度顺序。
 * 你看到的输入格式主要是为了确保测试的全面性。
 * <p>
 * <p>
 * 示例 1：
 * <p>
 * 输入：nums = [1,2,3]
 * 输出："firstsecondthird"
 * 解释：
 * 有三个线程会被异步启动。输入 [1,2,3] 表示线程 A 将会调用 first() 方法，线程 B 将会调用 second() 方法，线程 C 将会调用 third() 方法。正确的输出是 "firstsecondthird"。
 * 示例 2：
 * <p>
 * 输入：nums = [1,3,2]
 * 输出："firstsecondthird"
 * 解释：
 * 输入 [1,3,2] 表示线程 A 将会调用 first() 方法，线程 B 将会调用 third() 方法，线程 C 将会调用 second() 方法。正确的输出是 "firstsecondthird"。
 * <p>
 * <p>
 * 提示：
 * nums 是 [1, 2, 3] 的一组排列
 */
@SuppressWarnings("all")
class Foo {
    //不用volatile是因为synchronize已经保证的可见性
    int flag = 1;
    final Object object = new Object();


    public Foo() {

    }

    public void first(Runnable printFirst) throws InterruptedException {
        synchronized (object) {
            while (flag != 1) {
                object.wait();
            }
            // printFirst.run() outputs "first". Do not change or remove this line.
            printFirst.run();
            flag = 2;
            object.notifyAll();
        }


    }

    public void second(Runnable printSecond) throws InterruptedException {
        synchronized (object) {
            while (flag != 2) {
                object.wait();
            }
            // printSecond.run() outputs "second". Do not change or remove this line.
            printSecond.run();
            flag = 3;
            object.notifyAll();
        }


    }

    public void third(Runnable printThird) throws InterruptedException {
        synchronized (object) {
            while (flag != 3) {
                object.wait();
            }
            // printThird.run() outputs "third". Do not change or remove this line.
            printThird.run();
        }

    }

}
