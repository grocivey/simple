package com.simple.api.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

@ConditionalOnProperty(name = "settings.enable",havingValue = "true")
@Slf4j
@Component
public class RedisDelayQueueTask implements ApplicationRunner {

    public static final String SETTING_DELAY_KEY = "setting_delay_key";
//    @Resource(name = "myStringRedisTemplate")
//    private RedisTemplate<String, Object> redisTemplate;


    @Override
    public void run(ApplicationArguments args)  {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                // 未配置fsu响应超时时间或配置格式错误，取默认值30s
                int  timeout = 30;
                long nowT = Instant.now().getEpochSecond() - timeout;
                System.out.println(30);
                try {
                    int i=0;
                    System.out.println(1/i);
//                    Set<Object> taskIds = redisTemplate.opsForZSet().rangeByScore(SETTING_DELAY_KEY, 0, nowT);
                    Set<Object> taskIds = null;
                    if (taskIds != null && !taskIds.isEmpty()) {
                        //找出超时的记录
                        System.out.println(taskIds);
                        //移除zset成员
//                        redisTemplate.opsForZSet().removeRangeByScore(SETTING_DELAY_KEY, 0, nowT);
                        //更新表数据,超时未响应
                    }
                } catch (NumberFormatException e) {
                    log.error("redis延时队列异常一次,",e);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.info("执行redis一次");
            }
        }, 100, 2000);
    }

    public static void main(String[] args) {
        new RedisDelayQueueTask().run(null);
    }
}
