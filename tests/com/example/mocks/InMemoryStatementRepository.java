package com.example.mocks;

import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.repository.StatementRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryStatementRepository implements StatementRepository {
    private final Map<String, StatementAggregate> store = new HashMap<>();

    @Override
    public Optional<StatementAggregate> findById(String statementId) {
        return Optional.ofNullable(store.get(statementId));
    }

    @Override
    public void save(StatementAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }
}
