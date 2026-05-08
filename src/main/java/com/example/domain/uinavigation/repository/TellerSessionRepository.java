package com.example.domain.uinavigation.repository;

import com.example.domain.teller.model.TellerSessionAggregate;

import java.util.Optional;

public interface TellerSessionRepository {
    TellerSessionAggregate save(TellerSessionAggregate aggregate);
    Optional<TellerSessionAggregate> findById(String id);
}