package com.example.authenticator.controllers;

import com.example.authenticator.dtos.*;
import com.example.authenticator.dtos.user.CreateUserResponse;
import com.example.authenticator.dtos.user.ResetPasswordResponse;
import com.example.authenticator.dtos.user.UserRequest;
import com.example.authenticator.enums.ResultCodeEnum;
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
    public ResponseEntity<Result<CreateUserResponse>> create(@RequestBody UserRequest userRequest) {

        var result = userService.create(userRequest);

        if (!result.failed()) {

            if (userRequest.withTemporaryPassword()) {

                var resetResult = passwordService.resetPassword(userRequest.username());

                if (resetResult.getCode().failed()) {

                    var removeCode = userService.remove(userRequest.username());

                    if (removeCode.failed())
                        return responseService.getResponse(ResultCodeEnum.ERROR_CODE);
                }
                else if (passwordService.isEnabledShowTemporaryPassword()) {
                    var data = new CreateUserResponse(resetResult.getData().temporaryPassword());
                    return responseService.getResponse(result, data);
                }

            }

        }

        return responseService.getResponse(result);
    }

    @PostMapping("/{userId}/reset")
    public ResponseEntity<Result<ResetPasswordResponse>> resetPassword(@PathVariable("userId") String userId) {

        var result = passwordService.resetPassword(userId);

        return responseService.getResponse(result);
    }
}
