package com.simple.api.controller;


import com.simple.api.util.RedisUtil;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/redis")
public class RedisLockController {
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @SneakyThrows
    @GetMapping(value = "/testLock")
    @ApiOperation(value = "测试redis分布式锁")
    public Object order() {

        redisTemplate.opsForValue().setIfAbsent("gzy:lock2", 1,10, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set("gzy:stock",100);
        int nowV = RedisUtil.getValueByKey(redisTemplate,"gzy:stock",Integer.class);
        System.out.println("当前值："+nowV);
        ExecutorService service = Executors.newFixedThreadPool(20);
        for (int i = 0; i <12; i++) {
            int finalI = i;
            service.execute(() -> {
                boolean lock = RedisUtil.lock(redisTemplate, RedisUtil.LOCK_KEY + "L1", finalI, 60);
                while (!lock){
                    try {
                        Thread.sleep(10);
                        lock = RedisUtil.lock(redisTemplate, RedisUtil.LOCK_KEY + "L1", finalI, 60);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                int stock = RedisUtil.getValueByKey(redisTemplate,"gzy:stock",Integer.class);

                if (stock > 0) {
                    redisTemplate.opsForValue().set("gzy:stock",--stock);
                }
                RedisUtil.unlock(redisTemplate, RedisUtil.LOCK_KEY + "L1", finalI);
            });

        }
        service.shutdown();
        while (!service.awaitTermination(1,TimeUnit.MILLISECONDS)){}
        return redisTemplate.opsForValue().get("gzy:stock");
    }


    @GetMapping(value = "/setRedisValue")
    public Object setRedisValue() {
        redisTemplate.opsForValue().set("gzy:stock",100);
        return RedisUtil.getValueByKey(redisTemplate, "gzy:stock",Integer.class);
    }

    @GetMapping(value = "/incre")
    public Object incre() {
        return redisTemplate.opsForValue().increment("fsu:upgrade:tmp:table11");
    }
}
