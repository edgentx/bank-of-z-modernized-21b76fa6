package com.example.infrastructure.mongo.statement;

import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.repository.StatementRepository;
import com.example.infrastructure.mongo.support.AggregateFieldAccess;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MongoStatementRepository implements StatementRepository {

  private final StatementMongoDataRepository data;

  public MongoStatementRepository(StatementMongoDataRepository data) {
    this.data = data;
  }

  @Override
  public Optional<StatementAggregate> findById(String statementId) {
    return data.findById(statementId).map(this::toAggregate);
  }

  @Override
  public void save(StatementAggregate aggregate) {
    data.save(toDocument(aggregate));
  }

  StatementDocument toDocument(StatementAggregate agg) {
    StatementDocument doc = new StatementDocument();
    doc.setId(agg.id());
    doc.setAccountNumber(agg.getAccountNumber());
    doc.setGenerated(agg.isGenerated());
    doc.setExported(agg.isExported());
    doc.setExportedFormat(agg.getExportedFormat());
    doc.setVersion(agg.getVersion());
    return doc;
  }

  StatementAggregate toAggregate(StatementDocument doc) {
    StatementAggregate agg = new StatementAggregate(doc.getId());
    AggregateFieldAccess.set(agg, "accountNumber", doc.getAccountNumber());
    AggregateFieldAccess.set(agg, "generated", doc.isGenerated());
    AggregateFieldAccess.set(agg, "exported", doc.isExported());
    AggregateFieldAccess.set(agg, "exportedFormat", doc.getExportedFormat());
    AggregateFieldAccess.set(agg, "version", doc.getVersion());
    return agg;
  }
}
