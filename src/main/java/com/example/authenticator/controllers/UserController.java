package com.example.authenticator.controllers;

import com.example.authenticator.dtos.Result;
import com.example.authenticator.dtos.ResultStatus;
import com.example.authenticator.enums.ResultCodeEnum;
import com.example.authenticator.services.PasswordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/users")
public class UserController {

    private final PasswordService passwordService;
    private final HashMap<ResultCodeEnum, ResultStatus> responses;

    public UserController(PasswordService passwordService) {
        this.passwordService = passwordService;
        this.responses = getResponses();
    }

    private HashMap<ResultCodeEnum, ResultStatus> getResponses() {

        final HashMap<ResultCodeEnum, ResultStatus> responses = new HashMap<>();

        responses.put(ResultCodeEnum.SUCCESS_CODE,
                new ResultStatus(
                        "The user's password has been successfully reset. He will be notified.",
                        HttpStatus.OK));

        responses.put(ResultCodeEnum.ERROR_CODE,
                new ResultStatus(
                        "An unknown error has ocurred. Try again later, if the error persists, contact the administrator.",
                        HttpStatus.INTERNAL_SERVER_ERROR));

        responses.put(ResultCodeEnum.ERROR_TEMP_ALREADY_ISSUED_CODE,
                new ResultStatus(
                        "The user's password has been reset since few minutes ago. Try again later.",
                        HttpStatus.TOO_MANY_REQUESTS));

        responses.put(ResultCodeEnum.ERROR_NOTIFICATION_CODE,
                new ResultStatus(
                        "The notification service is UNAVAILABLE now. Try again later.",
                        HttpStatus.SERVICE_UNAVAILABLE));

        responses.put(ResultCodeEnum.ERROR_TEMP_USER_BLOCKED,
                new ResultStatus(
                        "User was temporalily blocked. Try again later.",
                        HttpStatus.UNAUTHORIZED));

        return responses;
    }

    @PostMapping("/{userId}/reset")
    public ResponseEntity<Result> createTemporaryPassword(@PathVariable("userId") String userId) {

        var code = passwordService.resetPassword(userId);

        return getResetResponse(code);
    }

    private ResponseEntity<Result> getResetResponse(ResultCodeEnum code) {

        var response = responses.get(code);

        return new ResponseEntity<>(new Result(code.getValue(), response.message()), response.http());

    }
}
