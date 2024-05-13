package com.example.demo.services;

import com.example.demo.exceptions.RSAServerException;
import com.example.demo.security.RSACrypt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;

@Service
public class SecurityService {

    private final PrivateKey privateKey;

    public SecurityService(@Value("${security.private.key}") String privateKey) {
        this.privateKey = RSACrypt.readRSAPrivateKeyFromPEM(privateKey);
    }

    public String decrypt(@NonNull String encryptedPassword) throws SecurityException, RSAServerException {
        return RSACrypt.decrypt(encryptedPassword, privateKey);
    }
}
