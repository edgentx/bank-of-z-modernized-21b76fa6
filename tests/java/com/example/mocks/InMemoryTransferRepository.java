package com.example.mocks;
import com.example.domain.transfer.model.TransferAggregate;
import com.example.domain.transfer.repository.TransferRepository;
import java.util.*;
public class InMemoryTransferRepository implements TransferRepository {
    private final Map<String, TransferAggregate> store = new HashMap<>();
    @Override public void save(TransferAggregate a) { store.put(a.id(), a); }
    @Override public Optional<TransferAggregate> findById(String id) { return Optional.ofNullable(store.get(id)); }
}