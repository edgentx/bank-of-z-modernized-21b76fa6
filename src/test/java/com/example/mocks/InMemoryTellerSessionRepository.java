package com.example.mocks;

import com.example.domain.uimodel.TellerSessionAggregate;
import com.example.domain.uimodel.TellerSessionRepository;
import java.util.HashMap;
import java.util.Map;

public class InMemoryTellerSessionRepository implements TellerSessionRepository {
    private final Map<String, TellerSessionAggregate> store = new HashMap<>();

    @Override
    public TellerSessionAggregate getOrCreate(String id) {
        return store.computeIfAbsent(id, TellerSessionAggregate::new);
    }

    @Override
    public void save(TellerSessionAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }
}