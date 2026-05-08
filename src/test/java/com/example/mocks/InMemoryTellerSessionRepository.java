package com.example.mocks;

import com.example.domain.uimodel.model.TellerSessionAggregate;
import com.example.domain.uimodel.repository.TellerSessionRepository;

import java.util.HashMap;
import java.util.Map;

public class InMemoryTellerSessionRepository implements TellerSessionRepository {
    private final Map<String, TellerSessionAggregate> store = new HashMap<>();

    @Override
    public void save(TellerSessionAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    @Override
    public TellerSessionAggregate load(String id) {
        TellerSessionAggregate agg = store.get(id);
        if (agg == null) {
            return new TellerSessionAggregate(id);
        }
        return agg;
    }
}
