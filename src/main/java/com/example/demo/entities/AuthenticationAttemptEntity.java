package com.example.demo.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "attempts", timeToLive = 60)
public record AuthenticationAttemptEntity(
        @Id
        String userId,
        int attempts
) {

}
