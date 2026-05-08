package com.example.domain.tellermgmt.repository;

import com.example.domain.tellermgmt.model.TellerSessionAggregate;

import java.util.Optional;

/**
 * Repository interface for TellerSession aggregates.
 */
public interface TellerSessionRepository {
    void save(TellerSessionAggregate aggregate);
    Optional<TellerSessionAggregate> findById(String id);
}
