package com.example.mocks;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.repository.StatementRepository;
import java.util.*;
public class InMemoryStatementRepository implements StatementRepository {
    private final Map<String, StatementAggregate> store = new HashMap<>();
    @Override public void save(StatementAggregate a) { store.put(a.id(), a); }
    @Override public Optional<StatementAggregate> findById(String id) { return Optional.ofNullable(store.get(id)); }
}