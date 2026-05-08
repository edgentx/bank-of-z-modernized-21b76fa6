package com.example.domain.tellersession.repository;

import com.example.domain.tellersession.model.TellerSessionAggregate;
import org.springframework.stereotype.Repository;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class InMemoryTellerSessionRepository implements TellerSessionRepository {

    private final ConcurrentMap<String, TellerSessionAggregate> store = new ConcurrentHashMap<>();

    @Override
    public void save(TellerSessionAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    @Override
    public TellerSessionAggregate findById(String id) {
        return store.get(id);
    }
}
