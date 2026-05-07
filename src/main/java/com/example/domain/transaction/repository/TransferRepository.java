package com.example.domain.transaction.repository;

import com.example.domain.transaction.model.TransferAggregate;

/**
 * Repository interface for the Transfer Aggregate.
 */
public interface TransferRepository {
    void save(TransferAggregate aggregate);
    TransferAggregate load(String transferId);
}