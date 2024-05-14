package com.example.authenticator.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value="user")
public record UserEntity(
        @Id
        String username) {

}
