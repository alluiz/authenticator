package com.example.authenticator.dtos;

import org.springframework.http.HttpStatus;

public record ResultStatus(String message, HttpStatus http) {
}
