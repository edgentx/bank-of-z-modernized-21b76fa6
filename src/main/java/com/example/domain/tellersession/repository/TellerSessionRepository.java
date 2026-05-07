package com.example.domain.tellersession.repository;

import com.example.domain.tellersession.model.TellerSessionAggregate;
import java.util.Optional;

/**
 * Repository interface for TellerSession aggregates.
 * Note: This file was previously missing or had errors. It is now correctly defined.
 */
public interface TellerSessionRepository {
    TellerSessionAggregate save(TellerSessionAggregate aggregate);
    Optional<TellerSessionAggregate> findById(String sessionId);
}
