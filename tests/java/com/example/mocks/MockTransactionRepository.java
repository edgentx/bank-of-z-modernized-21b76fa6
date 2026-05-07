package com.example.mocks;

import com.example.domain.transaction.model.TransactionAggregate;
import com.example.domain.transaction.repository.TransactionRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class MockTransactionRepository implements TransactionRepository {
    private final Map<String, TransactionAggregate> store = new HashMap<>();

    @Override
    public TransactionAggregate save(TransactionAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
        return aggregate;
    }

    @Override
    public TransactionAggregate findById(String id) {
        return store.get(id);
    }
}
