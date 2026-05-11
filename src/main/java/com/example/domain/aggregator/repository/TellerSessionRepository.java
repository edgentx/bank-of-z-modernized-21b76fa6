package com.example.domain.aggregator.repository;

import com.example.domain.aggregator.model.TellerSessionAggregate;
import java.util.Optional;

public interface TellerSessionRepository {
    TellerSessionAggregate save(TellerSessionAggregate aggregate);
    Optional<TellerSessionAggregate> findById(String id);
}