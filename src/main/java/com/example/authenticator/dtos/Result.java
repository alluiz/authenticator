package com.example.authenticator.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Result<T>(String code, String message, Date operationTime, T data) {

}
