package com.example.authenticator.controllers;

import com.example.authenticator.dtos.authenticate.AuthenticateRequest;
import com.example.authenticator.dtos.authenticate.AuthenticateResponse;
import com.example.authenticator.dtos.Result;
import com.example.authenticator.services.ResponseService;
import com.example.authenticator.services.PasswordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authenticate")
public class AuthenticateController {

    private final ResponseService responseService;
    private final PasswordService passwordService;

    public AuthenticateController(ResponseService responseService, PasswordService passwordService) {
        this.responseService = responseService;
        this.passwordService = passwordService;
    }

    @PostMapping
    public ResponseEntity<Result<AuthenticateResponse>> authenticate(@RequestBody AuthenticateRequest authencationRequest) {

        var result = passwordService.authenticate(authencationRequest.username(), authencationRequest.password());

        return responseService.getResponse(result);
    }

}
