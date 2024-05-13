package com.example.demo.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "temp-password", timeToLive = 60)
public record TemporaryPasswordEntity(
        @Id
        String userId,
        String value) {
}
