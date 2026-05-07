package com.example.domain.transaction.repository;

import com.example.domain.transaction.model.TransactionAggregate;

import java.util.Optional;

public interface TransactionRepository {
    void save(TransactionAggregate aggregate);
    Optional<TransactionAggregate> findById(String id);
}
