package com.example.authenticator.controllers;

import com.example.authenticator.dtos.Result;
import com.example.authenticator.enums.ResultCodeEnum;
import com.example.authenticator.services.ResponseService;
import com.example.authenticator.services.PasswordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final ResponseService responseService;
    private final PasswordService passwordService;

    public UserController(ResponseService responseService, PasswordService passwordService) {
        this.responseService = responseService;
        this.passwordService = passwordService;
    }

    @PostMapping("/{userId}/reset")
    public ResponseEntity<Result> resetPassword(@PathVariable("userId") String userId) {

        var code = passwordService.resetPassword(userId);

        return getResetResponse(code);
    }

    private ResponseEntity<Result> getResetResponse(ResultCodeEnum code) {

        var response = responseService.getResponse(code);

        return new ResponseEntity<>(new Result(code.getValue(), response.message()), response.http());

    }
}
