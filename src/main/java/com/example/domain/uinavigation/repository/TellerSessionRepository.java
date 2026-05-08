package com.example.domain.uinavigation.repository;

import com.example.domain.uinavigation.model.TellerSessionAggregate;

import java.util.Optional;

public interface TellerSessionRepository {
    void save(TellerSessionAggregate aggregate);
    Optional<TellerSessionAggregate> findById(String id);
}