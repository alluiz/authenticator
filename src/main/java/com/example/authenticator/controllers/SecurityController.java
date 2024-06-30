package com.example.authenticator.controllers;

import com.example.authenticator.dtos.Result;
import com.example.authenticator.dtos.security.PublicKeyResponse;
import com.example.authenticator.services.SecurityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/security")
public class SecurityController extends BaseController  {

    private final SecurityService securityService;

    protected SecurityController(SecurityService securityService) {
        super();
        this.securityService = securityService;
    }

    @GetMapping("/publicKey")
    public ResponseEntity<Result<PublicKeyResponse>> getPublicKey() {

        var result = securityService.getPublicKey();

        return getResponse(result);
    }

}
