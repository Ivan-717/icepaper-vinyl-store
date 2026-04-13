package com.bingzhi.vinyl.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

//redis配置类：配置redis序列化方式，让存入Redis的数据可读
//序列化：将java对象转换成字节序列（二进制数据）
//redis:本身只存储二进制数据

@Configuration
public class RedisConfig {

    @Bean
    //RedisTemplate是spring提供的操作redis数据库的类
    //RedisConnectionFactory → 创建 → RedisConnection → 被 → RedisTemplate 使用
    //有工厂：由spring管理连接池，自动获取和归还连接
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory factory){

        //创建RedisTemplate实例
        RedisTemplate<String,Object> template=new RedisTemplate<>();

        //设置连接工厂
        template.setConnectionFactory(factory);

        //key使用string序列化
        template.setKeySerializer(new StringRedisSerializer());
        //value使用json序列化
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        // hash key 使用 String 序列化
        template.setHashKeySerializer(new StringRedisSerializer());
        // hash value 使用 JSON 序列化
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        //afterPropertiesSet():检查必要属性值有无设置，初始化内部状态
        template.afterPropertiesSet();
        return template;

    }
}
