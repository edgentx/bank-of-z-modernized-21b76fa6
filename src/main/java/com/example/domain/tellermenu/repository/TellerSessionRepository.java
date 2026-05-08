package com.example.domain.tellermenu.repository;

import com.example.domain.tellermenu.model.TellerSessionAggregate;

import java.util.Optional;

/**
 * Repository interface for TellerSession aggregate.
 */
public interface TellerSessionRepository {
    TellerSessionAggregate save(TellerSessionAggregate aggregate);
    Optional<TellerSessionAggregate> findById(String id);
}
