package com.example.demo.exceptions;

public class RSAServerException extends RuntimeException {

    public RSAServerException(String message, Throwable e) {
        super(message, e);
    }

}
