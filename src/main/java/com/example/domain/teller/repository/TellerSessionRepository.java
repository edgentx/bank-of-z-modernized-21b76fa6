package com.example.domain.teller.repository;

import com.example.domain.teller.model.TellerSessionAggregate;
import java.util.Optional;

/**
 * Repository for TellerSessionAggregate.
 * Renamed from generic Aggregate to concrete class to fix compilation errors.
 */
public interface TellerSessionRepository {
    TellerSessionAggregate save(TellerSessionAggregate aggregate);
    Optional<TellerSessionAggregate> findById(String id);
}
