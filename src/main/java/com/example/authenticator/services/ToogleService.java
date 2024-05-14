package com.example.authenticator.services;

import lombok.Getter;

@Getter
public abstract class ToogleService {
    
    private final boolean enabled;

    protected ToogleService(boolean enabled) {
        this.enabled = enabled;
    }
    
}
