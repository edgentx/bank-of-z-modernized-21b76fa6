package com.example.domain.uimodel.repository;

import com.example.domain.uimodel.TellerSessionAggregate;

import java.util.Optional;

public interface TellerSessionRepository {
    void save(TellerSessionAggregate aggregate);
    Optional<TellerSessionAggregate> findById(String sessionId);
}
