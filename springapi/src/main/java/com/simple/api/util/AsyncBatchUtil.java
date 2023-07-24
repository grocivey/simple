package com.simple.api.util;

import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 批量操作工具
 */
public class AsyncBatchUtil {
    /**
     * 将list切割成多个list，返回切割后的list给consumer操作
     *
     * @param nThreads  线程数
     * @param onceCount      一批多少数据
     * @param originalList 原list
     * @param consumer  consumer
     * @param <T>
     */
    public static <T> void listToBatchOperation(int nThreads,int onceCount,List<T> originalList, Consumer<List<T>> consumer){
        listToBatchOperation(false,nThreads,onceCount,originalList, consumer);
    }

    /**
     * 将list切割成多个list，返回切割后的list给consumer操作
     *
     * @param needBlock 是否阻塞等待完成
     * @param nThreads  线程数
     * @param onceCount      一批多少数据
     * @param originalList 原list
     * @param consumer  consumer
     * @param <T>
     */
    @SneakyThrows
    public static <T> void listToBatchOperation(boolean needBlock, int nThreads, int onceCount, List<T> originalList, Consumer<List<T>> consumer){
        ExecutorService pool = Executors.newFixedThreadPool(nThreads);//线程池
        int originalSize = originalList.size();
        int groupCount = originalSize / onceCount;//有几组1000个
        int overCount = originalSize % onceCount;//不满一千剩下的
        if (groupCount > 0) {
            for (int i = 0; i < groupCount; i++) {
                List<T> tmpList = new ArrayList<>();
                for (int j = 0; j < onceCount; j++) {
                    tmpList.add(originalList.get(i * onceCount + j));
                }
                pool.execute(() -> consumer.accept(tmpList));
            }
            if (overCount != 0) {
                List<T> tmpList = new ArrayList<>();
                for (int i = groupCount * onceCount; i < originalSize; i++) {
                    tmpList.add(originalList.get(i));
                }
                pool.execute(() -> consumer.accept(tmpList));
            }
        } else {
            pool.execute(() -> consumer.accept(originalList));
        }
        pool.shutdown();
        //是否阻塞等待执行完毕
        if (needBlock){
            while (!pool.awaitTermination(1, TimeUnit.MILLISECONDS)){
            }
        }
    }

    /**
     * 传入总数后返回每组的index给consumer使用
     *
     * @param nThreads  线程数
     * @param onceCount 一批数量
     * @param total     总数
     * @param consumer
     */
    @SneakyThrows
    public static  void totalToGroupIndexOperation(int nThreads, int onceCount, int total, Consumer<Integer> consumer){
        ExecutorService pool = Executors.newFixedThreadPool(nThreads);//线程池
        int groupCount = total/onceCount;//有几组meta
        int overCount = total%onceCount;//不满剩下的
        if(groupCount > 0){
            for (int i = 0; i < groupCount; i++) {
                int finalI = i;
                pool.execute(() -> consumer.accept(finalI));
            }
            if (overCount != 0){
                pool.execute(() -> consumer.accept(groupCount));
            }
        }else {
            pool.execute(() -> consumer.accept(0));
        }
        pool.shutdown();//该方法不会马上关闭线程，但是会拒绝新线程的注册。
        while (!pool.awaitTermination(1, TimeUnit.MILLISECONDS)){
        }
    }


    private AsyncBatchUtil() {
    }


}
