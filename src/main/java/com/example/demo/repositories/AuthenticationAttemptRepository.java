package com.example.demo.repositories;

import com.example.demo.entities.AuthenticationAttemptEntity;
import org.springframework.data.repository.CrudRepository;

public interface AuthenticationAttemptRepository extends CrudRepository<AuthenticationAttemptEntity, String> {
}
