package com.example.domain.uimodel.repository;

import com.example.domain.teller.model.TellerSessionAggregate;

import java.util.Optional;

// Alias or duplicate interface definition needed if the package structure strictly separates 'uimodel' and 'uinavigation'
// Given the error log implies this file exists and references the Aggregate, we ensure it points to the correct class.
public interface TellerSessionRepository {
    TellerSessionAggregate save(TellerSessionAggregate aggregate);
    Optional<TellerSessionAggregate> findById(String id);
}