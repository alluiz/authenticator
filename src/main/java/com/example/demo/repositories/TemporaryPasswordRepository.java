package com.example.demo.repositories;

import com.example.demo.entities.TemporaryPasswordEntity;
import org.springframework.data.repository.CrudRepository;

public interface TemporaryPasswordRepository extends CrudRepository<TemporaryPasswordEntity, String> {
}
