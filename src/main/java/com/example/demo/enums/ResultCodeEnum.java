package com.example.demo.enums;

import lombok.Getter;

@Getter
public enum ResultCodeEnum {

    //SUCCESS = 0X
    SUCCESS_CODE("00"),
    SUCCESS_TEMP_CODE("01"),

    //ERRORS = 9X
    ERROR_CODE("90"),
    ERROR_USER_PASSWORD_CODE("91"),
    ERROR_TEMP_ALREADY_ISSUED_CODE("92"),
    ERROR_NOTIFICATION_CODE("93"),
    ERROR_TEMP_USER_BLOCKED("94");

    private final String value;

    ResultCodeEnum(String value) {
        this.value = value;
    }

}
