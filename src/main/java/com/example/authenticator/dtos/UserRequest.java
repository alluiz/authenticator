package com.example.authenticator.dtos;

public record UserRequest(String username, boolean withTemporaryPassword) {
}
