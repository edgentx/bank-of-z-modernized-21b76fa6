package com.example.mocks;

import com.example.domain.teller.model.TellerSessionAggregate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryTellerSessionRepository {
    private final Map<String, TellerSessionAggregate> store = new HashMap<>();

    public void save(TellerSessionAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    public Optional<TellerSessionAggregate> findById(String id) {
        // Return a copy or new instance to avoid state pollution in tests if needed,
        // but for aggregate tests, modifying the instance is expected.
        // However, standard repository pattern usually returns the same reference for in-memory.
        return Optional.ofNullable(store.get(id));
    }
}
