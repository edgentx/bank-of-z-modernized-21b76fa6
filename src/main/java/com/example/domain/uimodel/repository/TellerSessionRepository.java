package com.example.domain.uimodel.repository;

import com.example.domain.uimodel.model.TellerSessionAggregate;

import java.util.Optional;

public interface TellerSessionRepository {
    TellerSessionAggregate save(TellerSessionAggregate aggregate);
    Optional<TellerSessionAggregate> findById(String id);
}