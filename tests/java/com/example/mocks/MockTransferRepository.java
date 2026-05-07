package com.example.mocks;

import com.example.domain.transfer.model.TransferAggregate;
import com.example.domain.transfer.repository.TransferRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class MockTransferRepository implements TransferRepository {
    private final Map<String, TransferAggregate> store = new HashMap<>();

    @Override
    public TransferAggregate save(TransferAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
        return aggregate;
    }

    @Override
    public TransferAggregate findById(String id) {
        return store.get(id);
    }
}
