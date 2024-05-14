package com.example.authenticator.services;

import com.example.authenticator.enums.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {

    private final AttemptsService attemptsService;
    private final BlackListService blackListService;

    public UserService(AttemptsService attemptsService,
                       BlackListService blackListService) {
        this.attemptsService = attemptsService;
        this.blackListService = blackListService;
    }

    public ResultCodeEnum isUnlocked(String username) {

        if (attemptsService.check(username))
            return userBlockedByAttempts();

        if (blackListService.check(username))
            return userBlockedByBlackList();

        return ResultCodeEnum.SUCCESS_CODE;

    }

    public void notifyFailedAuthentication(String username) {
        attemptsService.notify(username);
    }

    private ResultCodeEnum userBlockedByBlackList() {

        log.info("User was blocked by blacklist.");
        return ResultCodeEnum.ERROR_USER_BLOCKED;

    }

    private ResultCodeEnum userBlockedByAttempts() {

        log.info("User was blocked by attempts.");
        return ResultCodeEnum.ERROR_TEMP_USER_BLOCKED;

    }
}
