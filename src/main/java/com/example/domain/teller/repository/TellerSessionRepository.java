package com.example.domain.teller.repository;

import com.example.domain.teller.model.TellerSessionAggregate;

/**
 * Repository interface for TellerSession aggregate.
 */
public interface TellerSessionRepository {
    TellerSessionAggregate load(String id);
    void save(TellerSessionAggregate aggregate);
}
