package com.example.domain.transaction.repository;

import com.example.domain.transaction.model.TransactionAggregate;

import java.util.Optional;

/**
 * Repository interface for Transaction Aggregate.
 */
public interface TransactionRepository {
    TransactionAggregate save(TransactionAggregate aggregate);
    TransactionAggregate findById(String id); // Returns null or throws if not found, per style
}