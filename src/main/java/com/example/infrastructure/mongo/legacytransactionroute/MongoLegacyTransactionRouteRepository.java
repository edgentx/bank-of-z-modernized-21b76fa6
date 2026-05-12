package com.example.infrastructure.mongo.legacytransactionroute;

import com.example.domain.legacytransactionroute.model.LegacyTransactionRouteAggregate;
import com.example.domain.legacytransactionroute.repository.LegacyTransactionRouteAggregateRepository;
import com.example.infrastructure.mongo.support.AggregateFieldAccess;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MongoLegacyTransactionRouteRepository implements LegacyTransactionRouteAggregateRepository {

  private final LegacyTransactionRouteMongoDataRepository data;

  public MongoLegacyTransactionRouteRepository(LegacyTransactionRouteMongoDataRepository data) {
    this.data = data;
  }

  @Override
  public Optional<LegacyTransactionRouteAggregate> findById(String routeId) {
    return data.findById(routeId).map(this::toAggregate);
  }

  @Override
  public void save(LegacyTransactionRouteAggregate aggregate) {
    data.save(toDocument(aggregate));
  }

  LegacyTransactionRouteDocument toDocument(LegacyTransactionRouteAggregate agg) {
    return new LegacyTransactionRouteDocument(agg.id(), agg.getVersion());
  }

  LegacyTransactionRouteAggregate toAggregate(LegacyTransactionRouteDocument doc) {
    LegacyTransactionRouteAggregate agg = new LegacyTransactionRouteAggregate(doc.getId());
    AggregateFieldAccess.set(agg, "version", doc.getVersion());
    return agg;
  }
}
