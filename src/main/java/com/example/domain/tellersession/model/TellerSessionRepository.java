package com.example.domain.tellersession.model;

import com.example.domain.shared.Aggregate;
import java.util.Optional;

/**
 * Repository interface for TellerSession aggregates.
 */
public interface TellerSessionRepository {
    void save(TellerSessionAggregate aggregate);
    Optional<TellerSessionAggregate> findById(String id);
}
