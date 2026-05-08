package com.example.domain.teller.repository;

import com.example.domain.teller.model.TellerSessionAggregate;

/**
 * Repository interface for TellerSession aggregates.
 * Following the Hexagonal Architecture pattern.
 */
public interface TellerSessionRepository {
    void save(TellerSessionAggregate aggregate);
    TellerSessionAggregate load(String id);
}