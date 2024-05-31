package com.example.authenticator.enums;

import lombok.Getter;

@Getter
public enum ResultCodeEnum {

    //SUCCESS = 0XX
    SUCCESS_CODE("000"),
    SUCCESS_RESET_CODE("001"),
    SUCCESS_NOTIFICATION_CODE("002"),
    SUCCESS_TEMP_CODE("003"),
    SUCCESS_CREATE_USER_CODE("004"),
    SUCCESS_DELETE_USER_CODE("005"),
    SUCCESS_AUTH_CODE("006"),

    //ERRORS = 1XX
    ERROR_CODE("100"),
    ERROR_USER_PASSWORD_CODE("101"),
    ERROR_TEMP_ALREADY_ISSUED_CODE("102"),
    ERROR_NOTIFICATION_CODE("103"),
    ERROR_TEMP_USER_BLOCKED("104"),
    ERROR_USER_BLOCKED_CODE("105"),
    ERROR_USER_ALREADY_EXISTS_CODE("106");

    private final String value;

    ResultCodeEnum(String value) {
        this.value = value;
    }

    public boolean failed() {
        return !this.value.startsWith("0"); //SUCCESS START WITH 0
    }
}
