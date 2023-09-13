package com.simple.reference;

import com.simple.api.entity.BaseStat;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/**
 * 引用测试
 */
public class PhantomTest {
    public static void main(String[] args) {
        BaseStat baseStat = new BaseStat();
        baseStat.setId("1");
        baseStat.setName("gro");

        BaseStat baseStat2 = new BaseStat();
        baseStat2.setId("1");
        baseStat2.setName("gro");
        ReferenceQueue refQueue = new ReferenceQueue<>();
        PhantomReference<BaseStat> p = new PhantomReference<>(baseStat, refQueue);
        WeakReference<BaseStat> weak = new WeakReference<>(baseStat2,refQueue);
        baseStat = null;
        baseStat2 = null;
        System.gc();
        try {
            // Remove是一个阻塞方法，可以指定timeout，或者选择一直阻塞
            Reference<BaseStat> ref = refQueue.remove(1000L);
            if (ref != null) {
                System.out.println(ref.get());
                System.out.println("do something baseStat");
                // do something
            }
            Reference<BaseStat> ref2 = refQueue.remove(1000L);
            if (ref != null) {
                System.out.println(ref2.get());
                System.out.println("do something baseStat2");
                // do something
            }
        } catch (InterruptedException e) {
            System.out.println("Handle it");
            // Handle it
        }
    }
}
