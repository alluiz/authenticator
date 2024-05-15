package com.example.authenticator.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class BlackListService extends ToogleService {

    private final List<String> values;

    public BlackListService(@Value("${service.blacklist.enabled}") boolean enabled,
                            @Value("${service.blacklist.value}") String value) {
        super(enabled & !value.trim().isEmpty());
        this.values = Arrays.stream(value.trim().split(",")).toList();
    }

    public boolean check(String username) {

        return this.isEnabled() && values.contains(username);

    }

}
