package com.tox.tox.pets.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        // First, create a custom ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        // Register the JavaTimeModule to handle Java 8 date/time types like LocalDate, OffsetDateTime
        objectMapper.registerModule(new JavaTimeModule());
        // Enable default typing. This will add a "@class" property to the JSON,
        // which Jackson will use to deserialize the JSON back to the correct Java object type.
        // LaissezFaireSubTypeValidator is used for security and allows any type.
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

        // Create a JSON serializer with the custom ObjectMapper
        GenericJackson2JsonRedisSerializer jsonRedisSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        // Create the cache configuration
        return RedisCacheConfiguration.defaultCacheConfig()
                // Set a default expiration time for cache entries (e.g., 30 minutes)
                .entryTtl(Duration.ofMinutes(30))
                // Configure the key serializer (keys will be simple strings)
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                // Configure the value serializer (values will be JSON)
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonRedisSerializer))
                // Disable caching of null values to prevent potential issues
                .disableCachingNullValues();
    }
}
