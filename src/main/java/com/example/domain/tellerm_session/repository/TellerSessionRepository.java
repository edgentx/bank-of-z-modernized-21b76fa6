package com.example.domain.tellerm_session.repository;

import com.example.domain.tellerm_session.model.TellerSessionAggregate;

/**
 * Repository interface for TellerSession aggregates.
 */
public interface TellerSessionRepository {
    TellerSessionAggregate findById(String id);
    void save(TellerSessionAggregate aggregate);
}
