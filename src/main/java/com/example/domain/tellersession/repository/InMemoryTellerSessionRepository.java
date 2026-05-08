package com.example.domain.tellersession.repository;

import com.example.domain.tellersession.model.TellerSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemoryTellerSessionRepository implements TellerSessionRepository {
    private final Map<UUID, TellerSession> store = new HashMap<>();

    @Override
    public TellerSession save(TellerSession aggregate) {
        UUID id = UUID.fromString(aggregate.id());
        store.put(id, aggregate);
        return aggregate;
    }

    @Override
    public Optional<TellerSession> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }
}
