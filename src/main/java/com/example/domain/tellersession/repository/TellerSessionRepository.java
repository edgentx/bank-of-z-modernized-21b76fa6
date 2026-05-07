package com.example.domain.tellersession.repository;

import com.example.domain.tellersession.model.TellerSessionAggregate;

/**
 * Repository interface for TellerSession aggregates.
 */
public interface TellerSessionRepository {
    TellerSessionAggregate save(TellerSessionAggregate aggregate);
    TellerSessionAggregate findById(String id);
}
