package com.example.authenticator.services;

import com.example.authenticator.entities.AuthenticationAttemptEntity;
import com.example.authenticator.entities.TemporaryPasswordEntity;
import com.example.authenticator.enums.ResultCodeEnum;
import com.example.authenticator.repositories.AuthenticationAttemptRepository;
import com.example.authenticator.repositories.TemporaryPasswordRepository;
import com.example.authenticator.security.HashCrypt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class PasswordService {

    public static final int MAX_ATTEMPTS = 3;

    private final TemporaryPasswordRepository temporaryPasswordRepository;
    private final AuthenticationAttemptRepository attemptsRepository;
    private final SecurityService securityService;
    private final NotificationService notificationService;

    public PasswordService(TemporaryPasswordRepository temporaryPasswordRepository,
                           AuthenticationAttemptRepository attemptsRepository,
                           SecurityService securityService,
                           NotificationService notificationService) {

        this.temporaryPasswordRepository = temporaryPasswordRepository;
        this.attemptsRepository = attemptsRepository;
        this.securityService = securityService;
        this.notificationService = notificationService;

    }

    public ResultCodeEnum authenticate(String username, String encryptedPassword) {

        try {

            log.info("Username: {}", username);

            log.info("Validating user in the main base.");

            int attemptsCount = getAttemptsCount(username);

            if (exceedAttempts(attemptsCount))
                return userBlockedByAttempts();

            var tempPassword = temporaryPasswordRepository.findById(username);

            if (tempPassword.isPresent()) {

                log.info("User exists.");
                log.info("User has a reset in progress.");

                try {

                    String password = securityService.decrypt(encryptedPassword);

                    if (HashCrypt.matches(password, tempPassword.get().value())) {
                        log.info("User was authenticated with success.");
                        return ResultCodeEnum.SUCCESS_TEMP_CODE;
                    }

                } catch (SecurityException e) {
                    log.error("User was input invalid password. It will be counted as an attempt.", e);
                }
            }

            attemptsRepository.save(new AuthenticationAttemptEntity(username, attemptsCount + 1));

            return ResultCodeEnum.ERROR_USER_PASSWORD_CODE;

        } catch (Exception e) {
            log.error("An unknown error has ocurred while authenticating.", e);
            return ResultCodeEnum.ERROR_CODE;
        }

    }

    public ResultCodeEnum resetPassword(String username) {

        try {

            if (exceedAttempts(username))
                return userBlockedByAttempts();

            if (tempPasswordHasIssued(username))
                return userHasResetProcess(username);

            String tempPassword = getTempPassword();

            String hashPassword = HashCrypt.hash(tempPassword);

            var tempPasswordEntity = new TemporaryPasswordEntity(username, hashPassword);

            temporaryPasswordRepository.save(tempPasswordEntity);

            if (notificationService.notify(tempPassword, username))
                return ResultCodeEnum.SUCCESS_CODE;
            else {
                temporaryPasswordRepository.deleteById(username);
                return ResultCodeEnum.ERROR_NOTIFICATION_CODE;
            }

        } catch (Exception e) {
            return ResultCodeEnum.ERROR_CODE;
        }

    }

    private ResultCodeEnum userHasResetProcess(String username) {
        log.info("User '{}' has already a reset process.", username);
        return ResultCodeEnum.ERROR_TEMP_ALREADY_ISSUED_CODE;
    }

    private boolean exceedAttempts(String username) {

        int attemptsCount = getAttemptsCount(username);

        return exceedAttempts(attemptsCount);
    }

    private boolean exceedAttempts(int attempts) {
        return attempts == MAX_ATTEMPTS;
    }

    private ResultCodeEnum userBlockedByAttempts() {
        log.info("User was blocked.");
        return ResultCodeEnum.ERROR_TEMP_USER_BLOCKED;
    }

    private int getAttemptsCount(String username) {

        var attempts = attemptsRepository.findById(username);

        return attempts.map(AuthenticationAttemptEntity::attempts).orElse(0);
    }

    private static String getTempPassword() {

        log.info("Generating temp password...");

        return UUID.randomUUID()
                .toString()
                .toUpperCase()
                .replaceAll("-", "");

    }

    private boolean tempPasswordHasIssued(String username) {

        return this.temporaryPasswordRepository.existsById(username);

    }
}
