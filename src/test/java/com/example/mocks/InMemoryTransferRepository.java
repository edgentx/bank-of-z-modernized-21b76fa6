package com.example.mocks;

import com.example.domain.transaction.model.TransferAggregate;
import com.example.domain.transaction.repository.TransferRepository;

import java.util.HashMap;
import java.util.Map;

/**
 * In-memory implementation of TransferRepository for testing.
 */
public class InMemoryTransferRepository implements TransferRepository {

    private final Map<String, TransferAggregate> store = new HashMap<>();

    @Override
    public void save(TransferAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    @Override
    public TransferAggregate load(String transferId) {
        TransferAggregate aggregate = store.get(transferId);
        if (aggregate == null) {
            // Return a fresh aggregate if it doesn't exist (testing convenience)
            return new TransferAggregate(transferId);
        }
        return aggregate;
    }
}