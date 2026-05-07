package com.example.domain.transaction.repository;

import com.example.domain.transaction.model.TransactionAggregate;

public interface TransactionRepository {
    TransactionAggregate save(TransactionAggregate aggregate);
    TransactionAggregate findById(String id);
}
