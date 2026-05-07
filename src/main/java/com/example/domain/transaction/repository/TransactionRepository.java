package com.example.domain.transaction.repository;

import com.example.domain.transaction.model.TransactionAggregate;

public interface TransactionRepository {
    TransactionAggregate load(String id);
    void save(TransactionAggregate aggregate);
}
