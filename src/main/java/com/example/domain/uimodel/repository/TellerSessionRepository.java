package com.example.domain.uimodel.repository;

import com.example.domain.uimodel.model.TellerSessionAggregate;

import java.util.Optional;

/**
 * Repository interface for TellerSession Aggregate.
 */
public interface TellerSessionRepository {
    void save(TellerSessionAggregate aggregate);
    Optional<TellerSessionAggregate> findById(String id);
}
