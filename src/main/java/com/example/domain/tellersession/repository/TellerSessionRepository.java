package com.example.domain.tellersession.repository;

import com.example.domain.teller.model.TellerSessionAggregate;
import java.util.Optional;

/**
 * Repository interface for TellerSession aggregates.
 * Bridge to persistence layer (e.g., MongoDB, DB2).
 */
public interface TellerSessionRepository {
    TellerSessionAggregate save(TellerSessionAggregate aggregate);
    Optional<TellerSessionAggregate> findById(String id);
}
