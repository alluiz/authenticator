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
    private final TemporaryPasswordRepository passwordRepository;

    private final SecurityService securityService;
    private final NotificationService notificationService;
    private final UserService userService;
    private final AttemptsService attemptsService;

    public PasswordService(TemporaryPasswordRepository temporaryPasswordRepository,
                           TemporaryPasswordRepository passwordRepository,
                           SecurityService securityService,
                           NotificationService notificationService,
                           UserService userService,
                           AttemptsService attemptsService) {

        this.temporaryPasswordRepository = temporaryPasswordRepository;
        this.passwordRepository = passwordRepository;
        this.securityService = securityService;
        this.notificationService = notificationService;
        this.userService = userService;
        this.attemptsService = attemptsService;
    }

    public ResultCodeEnum authenticate(String username, String encryptedPassword) {

        try {

            log.info("User who would like to authenticate: {}", username);

            var authorizationResult = userService.isAuthorized(username);

            if (authorizationResult.failed()) {
                return authorizationFailedResult(authorizationResult);
            }

            var hasIssuedTempPassword = temporaryPasswordRepository.existsById(username);

            var authenticationResult = hasIssuedTempPassword?
                    authenticateWithTemporaryPassword(username, encryptedPassword) :
                    authenticatePassword(username, encryptedPassword);

            if (authenticationResult.failed())
                attemptsService.notify(username);

            return authenticationResult;

        } catch (Exception e) {
            log.error("An unknown error has ocurred while authenticating.", e);
            return ResultCodeEnum.ERROR_CODE;
        }

    }

    private static ResultCodeEnum authorizationFailedResult(ResultCodeEnum authorizationResult) {
        log.info("User is not authorized to perform this action. Check UserService logs for more details about it.");
        return authorizationResult;
    }

    public ResultCodeEnum resetPassword(String username) {

        try {

            log.info("User who would like to reset their own password: {}", username);

            var authorizationResult = userService.isAuthorized(username);

            if (authorizationResult.failed()) {
                return authorizationFailedResult(authorizationResult);
            }

            if (tempPasswordHasIssued(username))
                return userHasResetProcess(username);

            String tempPassword = createTemporaryPassword(username);

            var notificationResult = notificationService.notify(tempPassword, username);

            if (notificationResult.failed()) {
                removeTemporaryPassword(username);
                return notificationResult;
            }

            return ResultCodeEnum.SUCCESS_RESET_CODE;

        } catch (Exception e) {
            log.error("An unknown error has ocurred while resetting.", e);
            return ResultCodeEnum.ERROR_CODE;
        }

    }

    private String createTemporaryPassword(String username) {

        String tempPassword = generateTemporaryPassword();
        String hashPassword = HashCrypt.hash(tempPassword);
        saveTemporaryPassword(username, hashPassword);

        return tempPassword;

    }

    private void saveTemporaryPassword(String username, String hashPassword) {
        log.info("Saving temporary password.");
        var tempPasswordEntity = new TemporaryPasswordEntity(username, hashPassword);
        temporaryPasswordRepository.save(tempPasswordEntity);
    }

    private void removeTemporaryPassword(String username) {
        log.info("Removing temporary password.");
        temporaryPasswordRepository.deleteById(username);
    }

    private ResultCodeEnum authenticatePassword(String username, String encryptedPassword) {

        var password = passwordRepository.findById(username);

        if (password.isEmpty())
            return ResultCodeEnum.ERROR_USER_PASSWORD_CODE;

        return authenticateHash(encryptedPassword, password.get().value());

    }

    private ResultCodeEnum authenticateWithTemporaryPassword(String username, String encryptedPassword) {

        var temporaryPassword = temporaryPasswordRepository.findById(username);

        if (temporaryPassword.isEmpty())
            return ResultCodeEnum.ERROR_USER_PASSWORD_CODE;

        return authenticateHash(encryptedPassword, temporaryPassword.get().value());

    }

    private ResultCodeEnum authenticateHash(String encryptedPassword, String passwordHash) {

        try {

            String password = securityService.decrypt(encryptedPassword);

            if (HashCrypt.matches(password, passwordHash)) {
                log.info("User was authenticated with success.");
                return ResultCodeEnum.SUCCESS_TEMP_CODE;
            }

        } catch (SecurityException e) {
            log.error("User was input invalid password. It will be counted as an attempt.", e);
        }

        return ResultCodeEnum.ERROR_USER_PASSWORD_CODE;

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
