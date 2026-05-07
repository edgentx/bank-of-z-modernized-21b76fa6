package com.example.domain.teller.repository;

import com.example.domain.teller.model.TellerSessionAggregate;

/**
 * Repository interface for TellerSession aggregates.
 */
public interface TellerSessionRepository {
    TellerSessionAggregate save(TellerSessionAggregate aggregate);
    TellerSessionAggregate findById(String sessionId);
}
