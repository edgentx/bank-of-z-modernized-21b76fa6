package com.example.domain.teller.repository;

import com.example.domain.teller.model.TellerSession;

import java.util.Optional;

/**
 * Repository interface for TellerSession aggregates.
 * Follows the Adapter pattern: Tests use MockTellerSessionRepository,
 * production uses a DB2/Mongo-backed implementation.
 */
public interface TellerSessionRepository {
    TellerSession save(TellerSession aggregate);
    Optional<TellerSession> findById(String id);
}