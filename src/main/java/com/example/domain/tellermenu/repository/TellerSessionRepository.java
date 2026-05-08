package com.example.domain.tellermenu.repository;

import com.example.domain.tellermenu.model.TellerSessionAggregate;

import java.util.Optional;

public interface TellerSessionRepository {
    void save(TellerSessionAggregate aggregate);
    Optional<TellerSessionAggregate> findById(String id);
}