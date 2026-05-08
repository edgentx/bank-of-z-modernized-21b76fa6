package com.example.domain.aggregator.repository;

import com.example.domain.aggregator.model.TellerSessionAggregate;
import java.util.Optional;

/**
 * Repository contract for TellerSession aggregates.
 */
public interface TellerSessionRepository {
    void save(TellerSessionAggregate aggregate);
    Optional<TellerSessionAggregate> findById(String id);
}
