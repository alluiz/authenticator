package com.example.authenticator.services;

import com.example.authenticator.entities.AuthenticationAttemptEntity;
import com.example.authenticator.repositories.AuthenticationAttemptRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AttemptsService extends ToogleService {

    private final int maxAttempts;

    private final AuthenticationAttemptRepository attemptsRepository;

    public AttemptsService(@Value("${service.attempts.enabled}") boolean enabled,
                           @Value("${service.attempts.value}") int maxAttempts,
                           AuthenticationAttemptRepository attemptsRepository) {
        super(enabled);
        this.maxAttempts = maxAttempts;
        this.attemptsRepository = attemptsRepository;
    }

    public boolean check(String username) {
        return this.isEnabled() && exceedAttempts(getAttemptsCount(username));
    }

    public void notify(String username) {

        var attemptsCount = getAttemptsCount(username);
        attemptsRepository.save(new AuthenticationAttemptEntity(username, attemptsCount + 1));

    }

    private boolean exceedAttempts(int attempts) {
        return attempts == maxAttempts;
    }

    private int getAttemptsCount(String username) {

        var attempts = attemptsRepository.findById(username);

        return attempts.map(AuthenticationAttemptEntity::attempts).orElse(0);
    }
}
