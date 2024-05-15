package com.example.authenticator.services;

import com.example.authenticator.dtos.user.UserRequest;
import com.example.authenticator.entities.UserEntity;
import com.example.authenticator.enums.ResultCodeEnum;
import com.example.authenticator.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final AttemptsService attemptsService;
    private final BlackListService blackListService;

    public UserService(UserRepository userRepository,
                       AttemptsService attemptsService,
                       BlackListService blackListService) {
        this.userRepository = userRepository;
        this.attemptsService = attemptsService;
        this.blackListService = blackListService;
    }

    public boolean exists(String username) {

        log.info("Checking if user exists...");
        var exists = userRepository.existsById(username);
        log.info("User exists: {}", exists);

        return exists;
    }

    public ResultCodeEnum create(UserRequest userRequest) {

        String username = userRequest.username();

        log.info("Creating user: {}", username);

        if (exists(username))
            return userAlreadyExistsResult();

        var user = new UserEntity(username);
        saveUser(user);

        return userWasCreatedResult();
    }

    public ResultCodeEnum isAuthorized(String username) {

        if (!exists(username))
            return userNotExists();

        if (attemptsService.check(username))
            return userBlockedByAttempts();

        if (blackListService.check(username))
            return userBlockedByBlackList();

        return ResultCodeEnum.SUCCESS_CODE;

    }

    public ResultCodeEnum remove(String username) {

        try {

            userRepository.deleteById(username);
            return ResultCodeEnum.SUCCESS_DELETE_USER_CODE;

        } catch (Exception e) {
            return ResultCodeEnum.ERROR_CODE;
        }

    }

    private ResultCodeEnum userAlreadyExistsResult() {
        log.error("User already exists.");
        return ResultCodeEnum.ERROR_USER_ALREADY_EXISTS_CODE;
    }

    private ResultCodeEnum userBlockedByAttempts() {

        log.info("User was blocked by attempts.");
        return ResultCodeEnum.ERROR_TEMP_USER_BLOCKED;

    }

    private ResultCodeEnum userBlockedByBlackList() {

        log.warn("User was blocked by blacklist.");
        return ResultCodeEnum.ERROR_USER_BLOCKED_CODE;

    }

    private ResultCodeEnum userNotExists() {

        log.info("User was not found.");
        return ResultCodeEnum.ERROR_USER_PASSWORD_CODE;

    }

    private ResultCodeEnum userWasCreatedResult() {
        log.info("User was created.");
        return ResultCodeEnum.SUCCESS_CREATE_USER_CODE;
    }

    private void saveUser(UserEntity user) {
        log.info("Saving user...");
        userRepository.save(user);
        log.info("User was saved.");
    }
}
