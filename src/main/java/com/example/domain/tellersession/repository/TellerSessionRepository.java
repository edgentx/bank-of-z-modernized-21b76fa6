package com.example.domain.tellersession.repository;

import com.example.domain.tellersession.model.TellerSessionAggregate;

import java.util.Optional;

/**
 * Repository interface for TellerSession aggregate.
 * Implementations will handle persistence (DB2/MongoDB).
 */
public interface TellerSessionRepository {
    void save(TellerSessionAggregate aggregate);
    Optional<TellerSessionAggregate> findById(String id);
}