package com.example.domain.tellerm_session.repository;

import com.example.domain.tellerm_session.model.TellerSessionAggregate;

import java.util.Optional;

/**
 * Repository interface for TellerSession aggregates.
 */
public interface TellerSessionRepository {
    TellerSessionAggregate save(TellerSessionAggregate aggregate);
    Optional<TellerSessionAggregate> findById(String id);
    void deleteById(String id);
}
