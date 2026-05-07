package com.example.mocks;

import com.example.domain.tellersession.model.TellerSession;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryTellerSessionRepository implements TellerSessionRepository {
    private final Map<String, TellerSession> store = new HashMap<>();

    @Override
    public void save(TellerSession aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    @Override
    public Optional<TellerSession> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }
}
