package com.example.mocks;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.teller.repository.TellerSessionRepository;
import java.util.*;
public class InMemoryTellerSessionRepository implements TellerSessionRepository {
    private final Map<String, TellerSessionAggregate> store = new HashMap<>();
    @Override public void save(TellerSessionAggregate a) { store.put(a.id(), a); }
    @Override public Optional<TellerSessionAggregate> findById(String id) { return Optional.ofNullable(store.get(id)); }
}