package com.example.authenticator.repositories;

import com.example.authenticator.entities.AuthenticationAttemptEntity;
import org.springframework.data.repository.CrudRepository;

public interface AuthenticationAttemptRepository extends CrudRepository<AuthenticationAttemptEntity, String> {
}
