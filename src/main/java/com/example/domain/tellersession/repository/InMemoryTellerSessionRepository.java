package com.example.domain.tellersession.repository;

import com.example.domain.shared.Aggregate;
import com.example.domain.teller.repository.TellerSessionRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryTellerSessionRepository implements TellerSessionRepository {

    private final Map<String, Aggregate> store = new HashMap<>();

    @Override
    public void save(Aggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    @Override
    public Aggregate load(String id) {
        return Optional.ofNullable(store.get(id))
                .orElseThrow(() -> new IllegalArgumentException("No aggregate found for id: " + id));
    }
}
