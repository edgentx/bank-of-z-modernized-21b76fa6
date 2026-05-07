package com.example.mocks;

import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.repository.StatementRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class MockStatementRepository implements StatementRepository {
    private final Map<String, StatementAggregate> store = new HashMap<>();

    @Override
    public StatementAggregate save(StatementAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
        return aggregate;
    }

    @Override
    public StatementAggregate findById(String id) {
        return store.get(id);
    }
}
