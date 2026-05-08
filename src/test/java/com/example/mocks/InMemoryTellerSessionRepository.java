package com.example.mocks;

import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.teller.repository.TellerSessionRepository;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryTellerSessionRepository implements TellerSessionRepository {

    private final Map<String, TellerSessionAggregate> store = new HashMap<>();
    private final Duration defaultTimeout;

    public InMemoryTellerSessionRepository(Duration defaultTimeout) {
        this.defaultTimeout = defaultTimeout;
    }

    @Override
    public void save(TellerSessionAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    @Override
    public Optional<TellerSessionAggregate> findById(String id) {
        // In a real scenario, we would deserialize. 
        // For this in-memory test, we return the live instance or reconstruct.
        // To allow test isolation/reconstruction in a simple in-memory map, 
        // we assume the tests handle the lifecycle or we return the instance directly.
        return Optional.ofNullable(store.get(id));
    }

    public TellerSessionAggregate createNew(String sessionId) {
        TellerSessionAggregate aggregate = new TellerSessionAggregate(sessionId, defaultTimeout);
        return aggregate;
    }
}
