package com.example.authenticator.controllers;

import com.example.authenticator.dtos.Result;
import com.example.authenticator.dtos.user.CreateUserResponse;
import com.example.authenticator.dtos.user.ResetPasswordResponse;
import com.example.authenticator.dtos.user.UserRequest;
import com.example.authenticator.enums.ResultCodeEnum;
import com.example.authenticator.services.PasswordService;
import com.example.authenticator.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController extends BaseController {

    private final PasswordService passwordService;
    private final UserService userService;

    public UserController(PasswordService passwordService, UserService userService) {
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
                        return getResponse(ResultCodeEnum.ERROR_CODE);
                    else
                        return getResponse(resetResult.getCode());
                }
                else if (passwordService.isEnabledShowTemporaryPassword()) {
                    var data = new CreateUserResponse(resetResult.getData().temporaryPassword());
                    return getResponse(result, data);
                }

            }

        }

        return getResponse(result);
    }

    @PostMapping("/{userId}/reset")
    public ResponseEntity<Result<ResetPasswordResponse>> resetPassword(@PathVariable("userId") String userId) {

        var result = passwordService.resetPassword(userId);

        return getResponse(result);
    }
}
