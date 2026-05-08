package com.example.domain.tellermessaging.repository;

import com.example.domain.tellermessaging.model.TellerSessionAggregate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for TellerSession aggregates.
 * To be implemented by adapters (e.g., JPA, Mongo).
 */
public interface TellerSessionRepository {
    TellerSessionAggregate save(TellerSessionAggregate aggregate);
    TellerSessionAggregate findById(String id);
    boolean existsById(String id);
}
