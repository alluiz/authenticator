package com.example.authenticator.services;

import com.example.authenticator.enums.ResultCodeEnum;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    public ResultCodeEnum notify(String message, String destiny) {

        try {
            System.out.println("Hello, " + destiny + "! here is your message: " + message);
            return ResultCodeEnum.SUCCESS_NOTIFICATION_CODE;
        } catch (Exception e) {
            return ResultCodeEnum.ERROR_NOTIFICATION_CODE;
        }

    }

}
