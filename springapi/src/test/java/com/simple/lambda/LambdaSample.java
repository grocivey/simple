package com.simple.lambda;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * ForkJoinPool测试
 */
public class LambdaSample {
    @Test
    public void t1() throws InterruptedException {
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
        forkJoinPool.execute(()->IntStream.rangeClosed(1,10).parallel().forEach(i->{
            System.out.println(ThreadLocalRandom.current().nextInt(20));
        }));
        int parallelism = forkJoinPool.getParallelism();
        System.out.println(parallelism);
        Thread.sleep(1000);
        System.out.println("===============");
        List<List<Integer>> numbers = Arrays.asList(
                Arrays.asList(1, 2, 3),
                Arrays.asList(4, 5, 6),
                Arrays.asList(7, 8, 9)
        );
        List<Integer> flattenedNumbers = numbers.stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
        System.out.println(flattenedNumbers);
    }
}
