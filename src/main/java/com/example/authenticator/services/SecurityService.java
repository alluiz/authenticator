package com.example.authenticator.services;

import com.example.authenticator.exceptions.RSAServerException;
import com.example.authenticator.security.HashCrypt;
import com.example.authenticator.security.RSACrypt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.security.PrivateKey;

@Slf4j
@Service
public class SecurityService {

    private final FileService fileService;
    private final PrivateKey privateKey;

    public SecurityService(FileService fileService,
                           @Value("${service.security.private.key.path}") String privateKeyPath) {

        this.fileService = fileService;
        this.privateKey = loadPrivateKey(privateKeyPath);
    }

    public String decrypt(@NonNull String encryptedText) throws SecurityException, RSAServerException {
        return RSACrypt.decrypt(encryptedText, privateKey);
    }

    private PrivateKey loadPrivateKey(String privateKeyPath) {

        try {

            log.info("Loading private key from OS...");

            var privateKeyFile = fileService.open(privateKeyPath);
            var privateKey = RSACrypt.readRSAPrivateKeyFromPEM(privateKeyFile);

            log.info("Private key was loaded. SHA256: {}.", HashCrypt.hash(privateKeyFile));

            return privateKey;

        } catch (FileNotFoundException e) {
            throw new RSAServerException("Private key was not found. We aren´t able to decrypt any data.", e);
        }

    }
}
