package com.example.domain.transfer.repository;

import com.example.domain.transfer.model.TransferAggregate;

public interface TransferRepository {
    TransferAggregate save(TransferAggregate aggregate);
    TransferAggregate findById(String id);
}
