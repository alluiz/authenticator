package com.example.authenticator.dtos.user;

public record UserRequest(String username, boolean withTemporaryPassword) {
}
