package com.example.domain.teller.repository;

import com.example.domain.teller.model.TellerSessionAggregate;

import java.util.Optional;

/**
 * Repository interface for TellerSessionAggregate.
 */
public interface TellerSessionRepository {
    TellerSessionAggregate save(TellerSessionAggregate aggregate);
    Optional<TellerSessionAggregate> findById(String id);
    // In-memory impl helper
    TellerSessionAggregate getOrCreate(String id);
}
