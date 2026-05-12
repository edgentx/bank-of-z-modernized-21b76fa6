package com.example.infrastructure.mongo.transfer;

import com.example.domain.transfer.model.TransferAggregate;
import com.example.domain.transfer.repository.TransferRepository;
import com.example.infrastructure.mongo.support.AggregateFieldAccess;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class MongoTransferRepository implements TransferRepository {

  private final TransferMongoDataRepository data;

  public MongoTransferRepository(TransferMongoDataRepository data) {
    this.data = data;
  }

  @Override
  public Optional<TransferAggregate> findById(String transferId) {
    return data.findById(transferId).map(this::toAggregate);
  }

  @Override
  public void save(TransferAggregate aggregate) {
    data.save(toDocument(aggregate));
  }

  TransferDocument toDocument(TransferAggregate agg) {
    TransferDocument doc = new TransferDocument();
    doc.setId(agg.id());
    doc.setFromAccountId(agg.getFromAccountId());
    doc.setToAccountId(agg.getToAccountId());
    doc.setStatus(agg.getStatus() != null ? agg.getStatus().name() : null);
    doc.setAmount(agg.getAmount());
    doc.setCurrency((String) AggregateFieldAccess.get(agg, "currency"));
    doc.setVersion(agg.getVersion());
    return doc;
  }

  TransferAggregate toAggregate(TransferDocument doc) {
    TransferAggregate agg = new TransferAggregate(doc.getId());
    AggregateFieldAccess.set(agg, "fromAccountId", doc.getFromAccountId());
    AggregateFieldAccess.set(agg, "toAccountId", doc.getToAccountId());
    AggregateFieldAccess.set(agg, "amount", doc.getAmount() != null ? doc.getAmount() : BigDecimal.ZERO);
    AggregateFieldAccess.set(agg, "currency", doc.getCurrency());
    AggregateFieldAccess.set(
        agg, "status",
        doc.getStatus() != null ? TransferAggregate.Status.valueOf(doc.getStatus()) : TransferAggregate.Status.NONE);
    AggregateFieldAccess.set(agg, "version", doc.getVersion());
    return agg;
  }
}
