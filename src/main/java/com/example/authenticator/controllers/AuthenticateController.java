package com.example.authenticator.controllers;

import com.example.authenticator.dtos.Result;
import com.example.authenticator.dtos.AuthenticationRequest;
import com.example.authenticator.enums.ResultCodeEnum;
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
    public ResponseEntity<Result> authenticate(@RequestBody AuthenticationRequest authencationRequest) {

        var code = passwordService.authenticate(authencationRequest.username(), authencationRequest.password());

        return getAuthenticationResponse(code);
    }

    private ResponseEntity<Result> getAuthenticationResponse(ResultCodeEnum code) {

        var response = responseService.getResponse(code);

        return new ResponseEntity<>(new Result(code.getValue(), response.message()), response.http());

    }

}
