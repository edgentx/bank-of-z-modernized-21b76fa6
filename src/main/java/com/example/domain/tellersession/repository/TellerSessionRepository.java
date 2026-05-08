package com.example.domain.tellersession.repository;

import com.example.domain.tellersession.model.TellerSessionAggregate;

/**
 * Repository interface for TellerSession aggregates.
 */
public interface TellerSessionRepository {
    void save(TellerSessionAggregate aggregate);
    TellerSessionAggregate load(String sessionId);
}
