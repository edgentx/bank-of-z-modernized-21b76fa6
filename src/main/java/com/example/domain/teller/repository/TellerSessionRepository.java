package com.example.domain.teller.repository;

import com.example.domain.teller.model.TellerSessionAggregate;

import java.util.Optional;

/**
 * Repository interface for Teller Session aggregates.
 * In a real implementation, this would be backed by Redis for speed
 * and DB2/MongoDB for audit.
 */
public interface TellerSessionRepository {
    void save(TellerSessionAggregate aggregate);
    Optional<TellerSessionAggregate> findById(String id);
}