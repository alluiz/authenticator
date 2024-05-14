package com.example.authenticator.dtos;

import java.util.Date;

public record ResultWithData<T>(String code, String message, Date operationTime, T data) {

}
