package com.example.domain.transfer.repository;
import com.example.domain.transfer.model.TransferAggregate;
import java.util.Optional;
public interface TransferRepository {
  Optional<TransferAggregate> findById(String transferId);
  void save(TransferAggregate aggregate);
}
