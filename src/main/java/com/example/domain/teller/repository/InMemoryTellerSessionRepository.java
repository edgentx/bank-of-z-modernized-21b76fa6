package com.example.domain.teller.repository;

import com.example.domain.teller.model.TellerSessionAggregate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryTellerSessionRepository implements TellerSessionRepository {

    private final Map<String, TellerSessionAggregate> store = new HashMap<>();

    @Override
    public TellerSessionAggregate load(String id) {
        // For testing purposes, return a new aggregate if not found,
        // or throw if strictly required. The test steps imply setup via Given.
        // We will return the stored instance or null/throw depending on strictness.
        // Here we return Optional-like behavior: if not found, return null to allow
        // the caller to instantiate, or return the instance.
        // Strict DDD usually throws if not found, but Given steps often handle setup.
        return store.get(id);
    }

    @Override
    public void save(TellerSessionAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }
}
