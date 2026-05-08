package com.example.repository;

import com.example.domain.navigation.model.TellerSessionAggregate;
import com.example.domain.navigation.repository.TellerSessionRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryTellerSessionRepository implements TellerSessionRepository {

    private final Map<String, TellerSessionAggregate> store = new HashMap<>();

    @Override
    public void save(TellerSessionAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    @Override
    public Optional<TellerSessionAggregate> findById(String id) {
        // Return a copy or new instance? For unit tests, returning the stored instance is usually fine
        // but strictly we might want to simulate loading. Let's return the instance we saved.
        return Optional.ofNullable(store.get(id));
    }
}
