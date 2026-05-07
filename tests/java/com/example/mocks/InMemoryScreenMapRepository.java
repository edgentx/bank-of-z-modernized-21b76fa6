package com.example.mocks;
import com.example.domain.screen.model.ScreenMapAggregate;
import com.example.domain.screen.repository.ScreenMapRepository;
import java.util.*;
public class InMemoryScreenMapRepository implements ScreenMapRepository {
    private final Map<String, ScreenMapAggregate> store = new HashMap<>();
    @Override public void save(ScreenMapAggregate a) { store.put(a.id(), a); }
    @Override public Optional<ScreenMapAggregate> findById(String id) { return Optional.ofNullable(store.get(id)); }
}