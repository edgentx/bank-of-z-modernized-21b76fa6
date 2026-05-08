package com.example.domain.tellersession.repository;

import com.example.domain.tellersession.model.TellerSessionAggregate;

/**
 * Repository interface for TellerSession aggregates.
 * Implementations (e.g., in-memory for tests, MongoDB for prod) will handle persistence.
 */
public interface TellerSessionRepository {
    TellerSessionAggregate save(TellerSessionAggregate aggregate);
    TellerSessionAggregate findById(String sessionId);
}
