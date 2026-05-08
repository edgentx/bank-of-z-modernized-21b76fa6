package com.example.domain.uinavigation.repository;

import com.example.domain.uinavigation.model.TellerSessionAggregate;

import java.util.Optional;

/**
 * Repository interface for TellerSession aggregates.
 * The errors indicated that TellerSessionAggregate was missing from this location.
 * This interface now correctly references the model class in the sibling 'model' package.
 */
public interface TellerSessionRepository {

    TellerSessionAggregate save(TellerSessionAggregate aggregate);

    Optional<TellerSessionAggregate> findById(String id);

    // delete etc. if needed
}