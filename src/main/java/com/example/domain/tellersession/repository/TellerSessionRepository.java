package com.example.domain.tellersession.repository;

import com.example.domain.tellersession.model.TellerSessionAggregate;

import java.util.Optional;

/**
 * Repository interface for TellerSessionAggregate.
 * In-memory implementation provided in test scope.
 */
public interface TellerSessionRepository {
    TellerSessionAggregate save(TellerSessionAggregate aggregate);
    Optional<TellerSessionAggregate> findById(String id);
    TellerSessionAggregate loadOrCreate(String id);
}
