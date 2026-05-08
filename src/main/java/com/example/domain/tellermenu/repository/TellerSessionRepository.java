package com.example.domain.tellermenu.repository;

import com.example.domain.tellermenu.model.TellerSessionAggregate;

/**
 * Repository interface for TellerSession aggregate.
 * Following the Hexagonal Architecture pattern.
 */
public interface TellerSessionRepository {
    TellerSessionAggregate save(TellerSessionAggregate aggregate);
    TellerSessionAggregate findById(String id);
}
