package com.example.infrastructure.mongo.transaction;

import com.example.domain.transaction.model.TransactionAggregate;
import com.example.domain.transaction.repository.TransactionRepository;
import com.example.infrastructure.mongo.support.AggregateFieldAccess;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class MongoTransactionRepository implements TransactionRepository {

  private final TransactionMongoDataRepository data;

  public MongoTransactionRepository(TransactionMongoDataRepository data) {
    this.data = data;
  }

  @Override
  public Optional<TransactionAggregate> findById(String transactionId) {
    return data.findById(transactionId).map(this::toAggregate);
  }

  @Override
  public void save(TransactionAggregate aggregate) {
    data.save(toDocument(aggregate));
  }

  TransactionDocument toDocument(TransactionAggregate agg) {
    TransactionDocument doc = new TransactionDocument();
    doc.setId(agg.id());
    doc.setAccountId(agg.getAccountId());
    doc.setKind(agg.getKind());
    doc.setAmount(agg.getAmount());
    // currency is not exposed via getter on the aggregate; read it reflectively.
    doc.setCurrency((String) AggregateFieldAccess.get(agg, "currency"));
    doc.setPosted(agg.isPosted());
    doc.setReversed(agg.isReversed());
    doc.setVersion(agg.getVersion());
    return doc;
  }

  TransactionAggregate toAggregate(TransactionDocument doc) {
    TransactionAggregate agg = new TransactionAggregate(doc.getId());
    AggregateFieldAccess.set(agg, "accountId", doc.getAccountId());
    AggregateFieldAccess.set(agg, "kind", doc.getKind());
    AggregateFieldAccess.set(agg, "amount", doc.getAmount() != null ? doc.getAmount() : BigDecimal.ZERO);
    AggregateFieldAccess.set(agg, "currency", doc.getCurrency());
    AggregateFieldAccess.set(agg, "posted", doc.isPosted());
    AggregateFieldAccess.set(agg, "reversed", doc.isReversed());
    AggregateFieldAccess.set(agg, "version", doc.getVersion());
    return agg;
  }
}
