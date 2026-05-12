package com.example.domain.datasynccheckpoint.repository;

import com.example.domain.datasynccheckpoint.model.DataSyncCheckpointAggregate;
import java.util.Optional;

public interface DataSyncCheckpointRepository {
  Optional<DataSyncCheckpointAggregate> findById(String checkpointId);
  void save(DataSyncCheckpointAggregate aggregate);
}
