package com.example.authenticator.services;

import com.example.authenticator.entities.AuthenticationAttemptEntity;
import com.example.authenticator.enums.ResultCodeEnum;
import com.example.authenticator.repositories.AuthenticationAttemptRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@Service
public class UserService {

    public static final int MAX_ATTEMPTS = 3;

    private final AuthenticationAttemptRepository attemptsRepository;

    public UserService(AuthenticationAttemptRepository attemptsRepository) {
        this.attemptsRepository = attemptsRepository;
    }

    public ResultCodeEnum isUnlocked(String username) {

        if (exceedAttempts(username))
            return userBlockedByAttempts();

        if (inBlackList(username))
            return userBlockedByBlackList();

        return ResultCodeEnum.SUCCESS_CODE;

    }

    private ResultCodeEnum userBlockedByBlackList() {

        log.info("User was blocked by blacklist.");
        return ResultCodeEnum.ERROR_USER_BLOCKED;

    }

    private ResultCodeEnum userBlockedByAttempts() {

        log.info("User was blocked by attempts.");
        return ResultCodeEnum.ERROR_TEMP_USER_BLOCKED;

    }

    private boolean inBlackList(String username) {

        var black = new ArrayList<String>();
        black.add("luiz");

        return black.contains(username);

    }

    private boolean exceedAttempts(String username) {

        int attemptsCount = getAttemptsCount(username);

        return exceedAttempts(attemptsCount);
    }

    private boolean exceedAttempts(int attempts) {
        return attempts == MAX_ATTEMPTS;
    }

    private int getAttemptsCount(String username) {

        var attempts = attemptsRepository.findById(username);

        return attempts.map(AuthenticationAttemptEntity::attempts).orElse(0);
    }

    public void notifyFailedAuthentication(String username) {

        var attemptsCount = getAttemptsCount(username);
        attemptsRepository.save(new AuthenticationAttemptEntity(username, attemptsCount + 1));

    }
}
