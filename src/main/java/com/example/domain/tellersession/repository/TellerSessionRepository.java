package com.example.domain.tellersession.repository;

import com.example.domain.tellersession.model.TellerSessionAggregate;
import java.util.Optional;

/**
 * Repository interface for TellerSession aggregates.
 * Maps to the shared domain contract.
 */
public interface TellerSessionRepository {
    TellerSessionAggregate save(TellerSessionAggregate aggregate);
    Optional<TellerSessionAggregate> findById(String id);
}
