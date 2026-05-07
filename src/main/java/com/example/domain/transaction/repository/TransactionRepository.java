package com.example.domain.transaction.repository;
import com.example.domain.transaction.model.TransactionAggregate;
import java.util.Optional;
public interface TransactionRepository {
  Optional<TransactionAggregate> findById(String transactionId);
  void save(TransactionAggregate aggregate);
}
