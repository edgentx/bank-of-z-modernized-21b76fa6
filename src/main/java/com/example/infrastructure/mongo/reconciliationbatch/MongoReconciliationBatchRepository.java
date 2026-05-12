package com.example.infrastructure.mongo.reconciliationbatch;

import com.example.domain.reconciliationbatch.model.ReconciliationBatchAggregate;
import com.example.domain.reconciliationbatch.repository.ReconciliationBatchAggregateRepository;
import com.example.infrastructure.mongo.support.AggregateFieldAccess;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MongoReconciliationBatchRepository implements ReconciliationBatchAggregateRepository {

  private final ReconciliationBatchMongoDataRepository data;

  public MongoReconciliationBatchRepository(ReconciliationBatchMongoDataRepository data) {
    this.data = data;
  }

  @Override
  public Optional<ReconciliationBatchAggregate> findById(String batchId) {
    return data.findById(batchId).map(this::toAggregate);
  }

  @Override
  public void save(ReconciliationBatchAggregate aggregate) {
    data.save(toDocument(aggregate));
  }

  ReconciliationBatchDocument toDocument(ReconciliationBatchAggregate agg) {
    return new ReconciliationBatchDocument(agg.id(), agg.getVersion());
  }

  ReconciliationBatchAggregate toAggregate(ReconciliationBatchDocument doc) {
    ReconciliationBatchAggregate agg = new ReconciliationBatchAggregate(doc.getId());
    AggregateFieldAccess.set(agg, "version", doc.getVersion());
    return agg;
  }
}
