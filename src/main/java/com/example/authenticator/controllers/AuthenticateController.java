package com.example.authenticator.controllers;

import com.example.authenticator.dtos.Result;
import com.example.authenticator.dtos.authenticate.AuthenticateRequest;
import com.example.authenticator.dtos.authenticate.AuthenticateResponse;
import com.example.authenticator.services.PasswordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authenticate")
public class AuthenticateController extends BaseController {

    private final PasswordService passwordService;

    public AuthenticateController(PasswordService passwordService) {
        super();
        this.passwordService = passwordService;
    }

    @PostMapping
    public ResponseEntity<Result<AuthenticateResponse>> authenticate(@RequestBody AuthenticateRequest authencationRequest) {

        var result = passwordService.authenticate(authencationRequest.username(), authencationRequest.password());

        return getResponse(result);
    }

}
