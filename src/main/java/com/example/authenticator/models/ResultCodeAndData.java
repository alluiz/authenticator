package com.example.authenticator.models;

import com.example.authenticator.enums.ResultCodeEnum;
import lombok.Getter;

@Getter
public class ResultCodeAndData<T> {

    private final ResultCodeEnum code;
    private final T data;

    public ResultCodeAndData(ResultCodeEnum code, T data) {
        this.code = code;
        this.data = data;
    }

    public ResultCodeAndData(ResultCodeEnum code) {
        this.code = code;
        this.data = null;
    }

}
