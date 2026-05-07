package com.example.mocks;
import com.example.domain.transaction.model.TransactionAggregate;
import com.example.domain.transaction.repository.TransactionRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
public class InMemoryTransactionRepository implements TransactionRepository {
  private final Map<String, TransactionAggregate> store = new HashMap<>();
  @Override public Optional<TransactionAggregate> findById(String id) { return Optional.ofNullable(store.get(id)); }
  @Override public void save(TransactionAggregate a) { store.put(a.id(), a); }
}
