package com.example.domain.tellermaintenance.repository;

import com.example.domain.tellermaintenance.model.TellerSessionAggregate;

import java.util.Optional;

/**
 * Repository interface for TellerSessionAggregate.
 */
public interface TellerSessionRepository {
    Optional<TellerSessionAggregate> findById(String id);
    void save(TellerSessionAggregate aggregate);
}
