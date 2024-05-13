package com.example.demo.dtos;

import org.springframework.http.HttpStatus;

public record ResultStatus(String message, HttpStatus http) {
}
