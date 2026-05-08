package com.example.mocks;

import com.example.domain.tellersession.model.TellerSession;
import com.example.domain.tellersession.repository.TellerSessionRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory implementation of TellerSessionRepository for testing.
 * S-20: Implement EndSessionCmd on TellerSession
 */
public class InMemoryTellerSessionRepository implements TellerSessionRepository {

    private final Map<String, TellerSession> store = new HashMap<>();

    @Override
    public void save(TellerSession session) {
        store.put(session.id(), session);
    }

    @Override
    public Optional<TellerSession> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }
}
