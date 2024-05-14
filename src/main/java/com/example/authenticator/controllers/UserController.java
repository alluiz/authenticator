package com.example.authenticator.controllers;

import com.example.authenticator.dtos.*;
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
    public ResponseEntity<ResultWithData<CreateUserResponse>> create(@RequestBody UserRequest userRequest) {

        var result = userService.create(userRequest);
        CreateUserResponse data = null;

        if (!result.failed() && userRequest.withTemporaryPassword()) {

            var resetResult = passwordService.resetPassword(userRequest.username());

            if (resetResult.code().failed()) {
                userService.remove(userRequest.username());
            }
            else if (passwordService.isEnabledShowTemporaryPassword())
                data = new CreateUserResponse(resetResult.data().temporaryPassword());
        }

        return responseService.getResponseWithData(result, data);
    }

    @PostMapping("/{userId}/reset")
    public ResponseEntity<ResultWithData<ResetPasswordResponse>> resetPassword(@PathVariable("userId") String userId) {

        var result = passwordService.resetPassword(userId);

        return responseService.getResponseWithData(result);
    }
}
