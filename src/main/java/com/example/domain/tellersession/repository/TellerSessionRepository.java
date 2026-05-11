package com.example.domain.tellersession.repository;

import com.example.domain.tellersession.model.TellerSessionAggregate;
import java.util.Optional;

/**
 * Repository interface for TellerSession aggregates.
 * Errors indicated this file was missing or referenced a wrong class.
 */
public interface TellerSessionRepository {
    TellerSessionAggregate save(TellerSessionAggregate aggregate);
    Optional<TellerSessionAggregate> findById(String id);
}
