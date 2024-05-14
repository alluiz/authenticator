package com.example.authenticator.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import java.time.Duration;

@Configuration
@EnableRedisRepositories
public class CustomRedisConfiguration {

    public static final long DEFAULT_MAX_TTL_SECONDS = 120;
    public static final long TEMP_PASS_MAX_TTL_SECONDS = 10;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {

        var redisConfig = new RedisStandaloneConfiguration("localhost", 6379);
        redisConfig.setPassword("eYVX7EwVmmxKPCDmwMtyKVge8oLd2t81");

        return new LettuceConnectionFactory(redisConfig);
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {

        var template = new RedisTemplate<String, String>();
        template.setConnectionFactory(connectionFactory);

        return template;
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {

        var cacheConfiguration = RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(DEFAULT_MAX_TTL_SECONDS))
                .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(cacheConfiguration)
                .withCacheConfiguration("temp-passwords", RedisCacheConfiguration
                        .defaultCacheConfig()
                        .entryTtl(Duration.ofSeconds(TEMP_PASS_MAX_TTL_SECONDS)))
                .build();
    }
}
