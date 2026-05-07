package com.example.domain.transfer.repository;

import com.example.domain.transfer.model.TransferAggregate;
import java.util.Optional;

public interface TransferRepository {
    TransferAggregate save(TransferAggregate aggregate);
    Optional<TransferAggregate> findById(String transferId);
}
