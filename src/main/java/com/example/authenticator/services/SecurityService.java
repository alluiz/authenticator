package com.example.authenticator.services;

import com.example.authenticator.dtos.security.PublicKeyResponse;
import com.example.authenticator.enums.ResultCodeEnum;
import com.example.authenticator.exceptions.RSAServerException;
import com.example.authenticator.models.ResultCodeAndData;
import com.example.authenticator.security.HashCrypt;
import com.example.authenticator.security.RSACrypt;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;

@Slf4j
@Service
public class SecurityService {

    private final FileService fileService;
    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private String publicKeyHash;

    public SecurityService(FileService fileService,
                           @Value("${service.security.key}") String privateKeyPath) {

        this.fileService = fileService;
        this.privateKey = loadPrivateKey(privateKeyPath);
        this.publicKey = loadPublicKey(privateKey);
    }

    private PublicKey loadPublicKey(PrivateKey privateKey) {

        log.info("Loading public key from private key...");

        var publicKey = RSACrypt.readRSAPublicKeyFromPrivateKey(privateKey);

        publicKeyHash = HashCrypt.hash(publicKey.toString());

        log.info("Public key was loaded. SHA256: {}.", publicKeyHash);

        return publicKey;

    }

    private PrivateKey loadPrivateKey(String privateKeyPath) {

        try {

            log.info("Loading private key from OS...");

            var privateKeyFile = fileService.open(privateKeyPath);
            var privateKey = RSACrypt.readRSAPrivateKeyFromPEM(privateKeyFile);

            String privateKeyHash = HashCrypt.hash(privateKeyFile);

            log.info("Private key was loaded. SHA256: {}.", privateKeyHash);

            return privateKey;

        } catch (FileNotFoundException e) {
            throw new RSAServerException("Private key was not found. We aren´t able to decrypt any data.", e);
        }

    }

    public String decrypt(@NonNull String encryptedText) throws SecurityException, RSAServerException {
        return RSACrypt.decrypt(encryptedText, privateKey);
    }

    public ResultCodeAndData<PublicKeyResponse> getPublicKey() {

        RSAKey jwk = new RSAKey.Builder((RSAPublicKey)this.publicKey)
                .keyID(publicKeyHash)
                .algorithm(JWSAlgorithm.RS256)
                .build();

        return new ResultCodeAndData<>(ResultCodeEnum.SUCCESS_CODE,
                new PublicKeyResponse(jwk.toJSONObject()));

    }
}
