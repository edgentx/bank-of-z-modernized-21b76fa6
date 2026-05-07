package com.example.domain.transfer.repository;

import com.example.domain.transfer.model.TransferAggregate;

import java.util.Optional;

public interface TransferRepository {
    void save(TransferAggregate aggregate);
    Optional<TransferAggregate> findById(String id);
}
