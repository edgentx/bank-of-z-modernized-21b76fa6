package com.example.domain.teller.repository;

import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.shared.Aggregate;

import java.util.Optional;

public interface TellerSessionRepository {
    TellerSessionAggregate save(TellerSessionAggregate aggregate);
    Optional<TellerSessionAggregate> findById(String id);
    // Delete is usually not part of Aggregate lifecycle in DDD, but often useful for tests.
    // void delete(String id); 
}