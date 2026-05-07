package com.example.domain.teller.repository;

import com.example.domain.tellersession.model.TellerSessionAggregate;

import java.util.Optional;

/**
 * Repository interface for TellerSession Aggregate.
 * Location: src/main/java/com/example/domain/teller/repository/TellerSessionRepository.java
 * Note: The package structure in this project places TellerSession in 'domain/tellersession/model'
 * but the repository interface was requested in 'domain/teller/repository'.
 */
public interface TellerSessionRepository {
    TellerSessionAggregate save(TellerSessionAggregate aggregate);
    Optional<TellerSessionAggregate> findById(String id);
    // Delete not typically used in event sourcing, but may be needed for in-memory cleanup
    void delete(String id);
}