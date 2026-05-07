package com.example.domain.teller.repository;

import com.example.domain.teller.model.TellerSessionAggregate;

import java.util.Optional;

/**
 * Repository interface for TellerSession aggregates.
 */
public interface TellerSessionRepository {
    TellerSessionAggregate save(TellerSessionAggregate aggregate);
    Optional<TellerSessionAggregate> findById(String sessionId);
    // In-memory specific method usually, but kept here for interface contract consistency if needed
    TellerSessionAggregate getOrCreate(String sessionId);
}
