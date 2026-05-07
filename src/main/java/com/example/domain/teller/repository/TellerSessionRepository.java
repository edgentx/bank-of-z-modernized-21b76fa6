package com.example.domain.teller.repository;

import com.example.domain.teller.model.TellerSessionAggregate;
import java.util.Optional;

/**
 * Repository interface for TellerSession aggregate.
 */
public interface TellerSessionRepository {
    Optional<TellerSessionAggregate> findById(String id);
    void save(TellerSessionAggregate aggregate);
    TellerSessionAggregate create(String id);
}
