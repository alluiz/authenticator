package com.example.demo.services;

import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    public boolean notify(String message, String destiny) {

        System.out.println("Hello, "+ destiny + "! here is your message: " + message);

        return true;
    }

}
