package com.example.authenticator.services;

import com.example.authenticator.dtos.ResultStatus;
import com.example.authenticator.enums.ResultCodeEnum;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class ResponseService {

    private final HashMap<ResultCodeEnum, ResultStatus> responses = new HashMap<>();

    public ResponseService() {
        includeResponses();
    }

    private void includeResponses() {

        includeSuccessResponses();
        includeErrorResponses();

    }

    private void includeErrorResponses() {

        responses.put(ResultCodeEnum.ERROR_CODE,
                new ResultStatus(
                        "An unknown error has ocurred. Try again later, if the error persists, contact the administrator.",
                        HttpStatus.INTERNAL_SERVER_ERROR));

        responses.put(ResultCodeEnum.ERROR_USER_PASSWORD_CODE,
                new ResultStatus(
                        "User and/or password is invalid.",
                        HttpStatus.UNAUTHORIZED));

        responses.put(ResultCodeEnum.ERROR_TEMP_ALREADY_ISSUED_CODE,
                new ResultStatus(
                        "The user's password has been reset since few minutes ago. Try again later.",
                        HttpStatus.TOO_MANY_REQUESTS));

        responses.put(ResultCodeEnum.ERROR_NOTIFICATION_CODE,
                new ResultStatus(
                        "The notification service is UNAVAILABLE now. Try again later.",
                        HttpStatus.SERVICE_UNAVAILABLE));

        responses.put(ResultCodeEnum.ERROR_USER_BLOCKED,
                new ResultStatus(
                        "User was blocked. Contact the administrator.",
                        HttpStatus.UNAUTHORIZED));

        responses.put(ResultCodeEnum.ERROR_TEMP_USER_BLOCKED,
                new ResultStatus(
                        "User was temporalily blocked. Try again later.",
                        HttpStatus.UNAUTHORIZED));
    }

    private void includeSuccessResponses() {

        responses.put(ResultCodeEnum.SUCCESS_CODE,
                new ResultStatus(
                        "User authenticated with success.",
                        HttpStatus.OK));

        responses.put(ResultCodeEnum.SUCCESS_TEMP_CODE,
                new ResultStatus(
                        "User authenticated with a temporary password. Must be changed before it expires.",
                        HttpStatus.OK));

        responses.put(ResultCodeEnum.SUCCESS_RESET,
                new ResultStatus(
                        "The user's password has been successfully reset. He will be notified.",
                        HttpStatus.OK));
    }

    public ResultStatus getResponse(ResultCodeEnum code) {
        return responses.get(code);
    }

}
