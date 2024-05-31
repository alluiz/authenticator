package com.example.authenticator.controllers;

import com.example.authenticator.services.ResponseService;

public abstract class BaseController {

    protected final ResponseService responseService;

    protected BaseController(ResponseService responseService) {
        this.responseService = responseService;
    }
}
