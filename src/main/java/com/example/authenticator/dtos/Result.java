package com.example.authenticator.dtos;

import java.util.Date;

public record Result(String code, String message, Date operationTime) {

}
