package com.example.infrastructure.mongo.tellersession;

import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import com.example.infrastructure.mongo.support.AggregateFieldAccess;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MongoTellerSessionRepository implements TellerSessionRepository {

  private final TellerSessionMongoDataRepository data;

  public MongoTellerSessionRepository(TellerSessionMongoDataRepository data) {
    this.data = data;
  }

  @Override
  public Optional<TellerSessionAggregate> findById(String sessionId) {
    return data.findById(sessionId).map(this::toAggregate);
  }

  @Override
  public void save(TellerSessionAggregate aggregate) {
    data.save(toDocument(aggregate));
  }

  TellerSessionDocument toDocument(TellerSessionAggregate agg) {
    TellerSessionDocument doc = new TellerSessionDocument();
    doc.setId(agg.id());
    doc.setStatus(agg.getStatus() != null ? agg.getStatus().name() : null);
    doc.setAuthenticated(agg.isAuthenticated());
    doc.setTimedOut(agg.isTimedOut());
    doc.setInactivityTimeoutRuleViolated(agg.isInactivityTimeoutRuleViolated());
    doc.setNavigationStateValid(agg.isNavigationStateValid());
    doc.setVersion(agg.getVersion());
    return doc;
  }

  TellerSessionAggregate toAggregate(TellerSessionDocument doc) {
    TellerSessionAggregate agg = new TellerSessionAggregate(doc.getId());
    agg.setAuthenticated(doc.isAuthenticated());
    agg.setTimedOut(doc.isTimedOut());
    agg.setInactivityTimeoutRuleViolated(doc.isInactivityTimeoutRuleViolated());
    agg.setNavigationStateValid(doc.isNavigationStateValid());
    AggregateFieldAccess.set(
        agg, "status",
        doc.getStatus() != null
            ? TellerSessionAggregate.Status.valueOf(doc.getStatus())
            : TellerSessionAggregate.Status.NONE);
    AggregateFieldAccess.set(agg, "version", doc.getVersion());
    return agg;
  }
}
