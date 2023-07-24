package com.simple.api.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * redisTemplate配置
 * @param <K>
 * @param <V>
 */
@Configuration
public class  RedisConfig<K,V> {
    @Bean
    public RedisTemplate<K, V> redisTemplate(RedisConnectionFactory factory) {

        RedisTemplate<K, V> template = new RedisTemplate<>();

        template.setConnectionFactory(factory);

        Jackson2JsonRedisSerializer<Object> jacksonJsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);

        ObjectMapper om = new ObjectMapper();

        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

        jacksonJsonRedisSerializer.setObjectMapper(om);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);

        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);

        // value序列化方式采用jackson
        template.setValueSerializer(jacksonJsonRedisSerializer);

        // hash的value序列化方式采用jackson
        template.setHashValueSerializer(jacksonJsonRedisSerializer);

        template.afterPropertiesSet();

        return template;

    }


}


