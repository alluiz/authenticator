package com.example.authenticator.models;

import com.example.authenticator.enums.ResultCodeEnum;

public record ResultCodeAndData<T>(ResultCodeEnum code, T data) {
}
