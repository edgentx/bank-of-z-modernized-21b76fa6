package com.example.domain.tellersession.repository;

import com.example.domain.tellersession.model.TellerSessionAggregate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of the TellerSessionRepository.
 * Used for testing and initial prototyping.
 */
@Repository
public class InMemoryTellerSessionRepository implements com.example.domain.teller.repository.TellerSessionRepository {

    private final Map<String, TellerSessionAggregate> store = new ConcurrentHashMap<>();

    @Override
    public TellerSessionAggregate save(TellerSessionAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
        return aggregate;
    }

    @Override
    public Optional<TellerSessionAggregate> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public void delete(String id) {
        store.remove(id);
    }
}