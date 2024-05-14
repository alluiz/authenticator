package com.example.authenticator.controllers;

import com.example.authenticator.dtos.Result;
import com.example.authenticator.dtos.UserRequest;
import com.example.authenticator.services.ResponseService;
import com.example.authenticator.services.PasswordService;
import com.example.authenticator.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final ResponseService responseService;
    private final PasswordService passwordService;
    private final UserService userService;

    public UserController(ResponseService responseService, PasswordService passwordService, UserService userService) {
        this.responseService = responseService;
        this.passwordService = passwordService;
        this.userService = userService;
    }

    @PostMapping()
    public ResponseEntity<Result> create(@RequestBody UserRequest userRequest) {

        var result = userService.create(userRequest);

        if (!result.failed()) {

            var resetResult = passwordService.resetPassword(userRequest.username());

            if (resetResult.failed()) {
                userService.remove(userRequest.username());
                return responseService.getResponse(resetResult);
            }
        }

        return responseService.getResponse(result);
    }

    @PostMapping("/{userId}/reset")
    public ResponseEntity<Result> resetPassword(@PathVariable("userId") String userId) {

        var code = passwordService.resetPassword(userId);

        return responseService.getResponse(code);
    }
}
