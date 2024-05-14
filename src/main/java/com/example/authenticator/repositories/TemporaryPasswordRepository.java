package com.example.authenticator.repositories;

import com.example.authenticator.entities.TemporaryPasswordEntity;
import org.springframework.data.repository.CrudRepository;

public interface TemporaryPasswordRepository extends CrudRepository<TemporaryPasswordEntity, String> {
}
