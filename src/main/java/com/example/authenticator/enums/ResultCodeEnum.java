package com.example.authenticator.enums;

import lombok.Getter;

@Getter
public enum ResultCodeEnum {

    //SUCCESS = 0XX
    SUCCESS_CODE("000"),
    SUCCESS_RESET("001"),
    SUCCESS_TEMP_CODE("002"),

    //ERRORS = 1XX
    ERROR_CODE("100"),
    ERROR_USER_PASSWORD_CODE("101"),
    ERROR_TEMP_ALREADY_ISSUED_CODE("102"),
    ERROR_NOTIFICATION_CODE("103"),
    ERROR_TEMP_USER_BLOCKED("104"),
    ERROR_USER_BLOCKED("105");

    private final String value;

    ResultCodeEnum(String value) {
        this.value = value;
    }

    public boolean failed() {
        return !this.value.startsWith("0"); //SUCCESS START WITH 0
    }
}
