package com.example.mocks;

import com.example.domain.transfer.model.TransferAggregate;
import com.example.domain.transfer.repository.TransferRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryTransferRepository implements TransferRepository {
    private final Map<String, TransferAggregate> store = new HashMap<>();

    @Override
    public void save(TransferAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    @Override
    public Optional<TransferAggregate> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }
}
