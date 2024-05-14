package com.example.authenticator.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class BlackListService extends ToogleService {

    public BlackListService(@Value("${service.attempts.enabled}") 
                               boolean enabled) {
        super(enabled);
    }

    public boolean check(String username) {
        
        var black = new ArrayList<String>();
        black.add("luiz");

        return this.isEnabled() && black.contains(username);

    }

}
