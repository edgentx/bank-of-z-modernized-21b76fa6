package com.example.infrastructure.mongo.datasynccheckpoint;

import com.example.domain.datasynccheckpoint.model.DataSyncCheckpointAggregate;
import com.example.domain.datasynccheckpoint.repository.DataSyncCheckpointRepository;
import com.example.infrastructure.mongo.support.AggregateFieldAccess;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MongoDataSyncCheckpointRepository implements DataSyncCheckpointRepository {

  private final DataSyncCheckpointMongoDataRepository data;

  public MongoDataSyncCheckpointRepository(DataSyncCheckpointMongoDataRepository data) {
    this.data = data;
  }

  @Override
  public Optional<DataSyncCheckpointAggregate> findById(String checkpointId) {
    return data.findById(checkpointId).map(this::toAggregate);
  }

  @Override
  public void save(DataSyncCheckpointAggregate aggregate) {
    data.save(toDocument(aggregate));
  }

  DataSyncCheckpointDocument toDocument(DataSyncCheckpointAggregate agg) {
    return new DataSyncCheckpointDocument(agg.id(), agg.getVersion());
  }

  DataSyncCheckpointAggregate toAggregate(DataSyncCheckpointDocument doc) {
    DataSyncCheckpointAggregate agg = new DataSyncCheckpointAggregate(doc.getId());
    AggregateFieldAccess.set(agg, "version", doc.getVersion());
    return agg;
  }
}
