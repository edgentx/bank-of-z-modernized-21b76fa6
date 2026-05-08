package com.example.mocks;

import com.example.domain.tellersession.model.TellerSession;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory implementation of TellerSessionRepository for testing purposes.
 * Acts as the Mock Adapter in the test suite.
 */
public class InMemoryTellerSessionRepository implements TellerSessionRepository {

    private final Map<String, TellerSession> store = new HashMap<>();

    @Override
    public void save(TellerSession aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    @Override
    public Optional<TellerSession> findById(String id) {
        // Return a copy or the actual instance depending on transactional needs.
        // For aggregate testing, returning the instance is usually sufficient.
        return Optional.ofNullable(store.get(id));
    }
}
