package com.example.domain.tellersession.repository;

import com.example.domain.tellersession.model.TellerSessionAggregate;

import java.util.Optional;

/**
 * Repository interface for TellerSession aggregate.
 */
public interface TellerSessionRepository {
    Optional<TellerSessionAggregate> findById(String id);
    void save(TellerSessionAggregate aggregate);
}
