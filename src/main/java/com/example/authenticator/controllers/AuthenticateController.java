package com.example.authenticator.controllers;

import com.example.authenticator.dtos.Result;
import com.example.authenticator.dtos.AuthenticationRequest;
import com.example.authenticator.dtos.ResultStatus;
import com.example.authenticator.enums.ResultCodeEnum;
import com.example.authenticator.services.PasswordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/authenticate")
public class AuthenticateController {

    private final PasswordService passwordService;
    private final HashMap<ResultCodeEnum, ResultStatus> responses;

    public AuthenticateController(PasswordService passwordService) {

        this.responses = getResponses();
        this.passwordService = passwordService;

    }

    private HashMap<ResultCodeEnum, ResultStatus> getResponses() {

        final HashMap<ResultCodeEnum, ResultStatus> responses = new HashMap<>();

        responses.put(ResultCodeEnum.SUCCESS_CODE,
                new ResultStatus(
                        "User authenticated with success.",
                        HttpStatus.OK));

        responses.put(ResultCodeEnum.SUCCESS_TEMP_CODE,
                new ResultStatus(
                        "User authenticated with a temporary password. Must be changed before it expires.",
                        HttpStatus.OK));

        responses.put(ResultCodeEnum.ERROR_USER_PASSWORD_CODE,
                new ResultStatus(
                        "User and/or password is invalid.",
                        HttpStatus.UNAUTHORIZED));


        responses.put(ResultCodeEnum.ERROR_TEMP_USER_BLOCKED,
                new ResultStatus(
                        "User was temporarily blocked. Try again later.",
                        HttpStatus.UNAUTHORIZED));

        responses.put(ResultCodeEnum.ERROR_CODE,
                new ResultStatus(
                        "An unknown error has ocurred. Try again later, if the error persists, contact the administrator.",
                        HttpStatus.INTERNAL_SERVER_ERROR));

        return responses;
    }

    @PostMapping
    public ResponseEntity<Result> authenticate(@RequestBody AuthenticationRequest authencationRequest) {

        var code = passwordService.authenticate(authencationRequest.username(), authencationRequest.password());

        return getAuthenticationResponse(code);
    }

    private ResponseEntity<Result> getAuthenticationResponse(ResultCodeEnum code) {

        var response = responses.get(code);

        return new ResponseEntity<>(new Result(code.getValue(), response.message()), response.http());

    }

}
