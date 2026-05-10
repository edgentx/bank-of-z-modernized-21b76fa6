package com.example.domain.tellersession.repository;

import com.example.domain.tellersession.model.TellerSessionAggregate;

import java.util.Optional;

/**
 * Repository interface for TellerSession aggregates.
 * Implementations (InMemory, JPA) handle persistence.
 */
public interface TellerSessionRepository {

    TellerSessionAggregate save(TellerSessionAggregate aggregate);

    Optional<TellerSessionAggregate> findById(String id);

    // In-memory specific helper, often hidden behind interface, but exposed here for test simplicity if needed
    // or strictly adhering to interface, implemented by InMemoryTellerSessionRepository
}
