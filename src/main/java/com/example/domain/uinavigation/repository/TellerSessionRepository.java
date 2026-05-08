package com.example.domain.uinavigation.repository;

import com.example.domain.uinavigation.model.TellerSessionAggregate;

/**
 * Repository interface for TellerSession aggregate.
 */
public interface TellerSessionRepository {
    TellerSessionAggregate save(TellerSessionAggregate aggregate);
    TellerSessionAggregate findById(String id);
}
