package com.simple.api.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class RedisUtil {

    public static String LOCK_KEY = "gzy:lock:";
    /**
     * 加锁
     *
     * @param redisTemplate
     * @param lockKey
     * @param requestId
     * @param expTime
     * @return
     * @param <K> key泛型
     * @param <V> 值泛型
     * @author gzy
     */
    public static <K,V> boolean lock(RedisTemplate<K,V> redisTemplate,K lockKey,V requestId,long expTime){
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(lockKey, requestId, expTime, TimeUnit.SECONDS));
    }

    /**
     * 解锁
     *
     * @param redisTemplate
     * @param lockKey
     * @param requestId
     * @return
     * @param <K> key泛型
     * @param <V> 值泛型
     * @author gzy
     */
    public static <K,V> boolean unlock(RedisTemplate<K,V> redisTemplate,K lockKey,V requestId){
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        RedisScript<Long> redisScript = new DefaultRedisScript<>(script,Long.class);
        Long result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), requestId);
        if (result != null){
            return result == 1L;
        }
        return false;

    }

    public static  <T> T getValueByKey(RedisTemplate<String, Object> redisTemplate,String key,Class<T> clazz){
        Object o = redisTemplate.opsForValue().get(key);
        return clazz.cast(o);
    }

    public static void main(String[] args) {
        System.out.println(unlock(new RedisTemplate<>(),null,null));
    }

}
