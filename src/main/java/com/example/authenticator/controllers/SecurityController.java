package com.example.authenticator.controllers;

import com.example.authenticator.dtos.Result;
import com.example.authenticator.dtos.security.PublicKeyResponse;
import com.example.authenticator.dtos.user.ResetPasswordResponse;
import com.example.authenticator.services.ResponseService;
import com.example.authenticator.services.SecurityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/security")
public class SecurityController extends BaseController  {

    private final SecurityService securityService;

    protected SecurityController(ResponseService responseService, SecurityService securityService) {
        super(responseService);
        this.securityService = securityService;
    }

    @GetMapping("/publicKey")
    public ResponseEntity<Result<PublicKeyResponse>> getPublicKey() {

        var result = securityService.getPublicKey();

        return responseService.getResponse(result);
    }

}
