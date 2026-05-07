package com.example.domain.transfer.repository;

import com.example.domain.transfer.model.TransferAggregate;

/**
 * Repository interface for TransferAggregate.
 * This follows the existing pattern defined in Customer/Transaction repositories.
 */
public interface TransferRepository {
    void save(TransferAggregate aggregate);
    TransferAggregate load(String id);
}
