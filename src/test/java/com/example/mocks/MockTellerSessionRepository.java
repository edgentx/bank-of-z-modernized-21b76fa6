package com.example.mocks;

import com.example.domain.teller.model.TellerSession;
import com.example.domain.teller.repository.TellerSessionRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Mock Adapter for TellerSessionRepository.
 * Used in tests to avoid DB2/MongoDB dependencies during unit testing.
 */
public class MockTellerSessionRepository implements TellerSessionRepository {

    private final Map<String, TellerSession> store = new HashMap<>();

    @Override
    public TellerSession save(TellerSession aggregate) {
        store.put(aggregate.id(), aggregate);
        return aggregate;
    }

    @Override
    public Optional<TellerSession> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }
}
