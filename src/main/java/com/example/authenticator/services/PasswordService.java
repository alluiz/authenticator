package com.example.authenticator.services;

import com.example.authenticator.entities.TemporaryPasswordEntity;
import com.example.authenticator.enums.ResultCodeEnum;
import com.example.authenticator.repositories.TemporaryPasswordRepository;
import com.example.authenticator.security.HashCrypt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class PasswordService {

    private final TemporaryPasswordRepository temporaryPasswordRepository;
    private final SecurityService securityService;
    private final NotificationService notificationService;
    private final UserService userService;
    private final AttemptsService attemptsService;

    public PasswordService(TemporaryPasswordRepository temporaryPasswordRepository,
                           SecurityService securityService,
                           NotificationService notificationService,
                           UserService userService,
                           AttemptsService attemptsService) {

        this.temporaryPasswordRepository = temporaryPasswordRepository;
        this.securityService = securityService;
        this.notificationService = notificationService;
        this.userService = userService;
        this.attemptsService = attemptsService;
    }

    public ResultCodeEnum authenticate(String username, String encryptedPassword) {

        try {

            log.info("Username: {}", username);

            log.info("Validating user in the main base.");

            var authorizationResult = userService.isAuthorized(username);

            if (authorizationResult.failed())
                return authorizationResult;

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

            attemptsService.notify(username);

            return ResultCodeEnum.ERROR_USER_PASSWORD_CODE;

        } catch (Exception e) {
            log.error("An unknown error has ocurred while authenticating.", e);
            return ResultCodeEnum.ERROR_CODE;
        }

    }

    public ResultCodeEnum resetPassword(String username) {

        try {

            var authorizationResult = userService.isAuthorized(username);

            if (authorizationResult.failed())
                return authorizationResult;

            if (tempPasswordHasIssued(username))
                return userHasResetProcess(username);

            String tempPassword = generateTemporaryPassword();

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

    private static String generateTemporaryPassword() {

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
