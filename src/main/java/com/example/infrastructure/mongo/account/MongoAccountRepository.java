package com.example.infrastructure.mongo.account;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.repository.AccountRepository;
import com.example.infrastructure.mongo.support.AggregateFieldAccess;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MongoAccountRepository implements AccountRepository {

  private final AccountMongoDataRepository data;

  public MongoAccountRepository(AccountMongoDataRepository data) {
    this.data = data;
  }

  @Override
  public Optional<AccountAggregate> findById(String accountId) {
    return data.findById(accountId).map(this::toAggregate);
  }

  @Override
  public void save(AccountAggregate aggregate) {
    data.save(toDocument(aggregate));
  }

  AccountDocument toDocument(AccountAggregate agg) {
    AccountDocument doc = new AccountDocument();
    doc.setId(agg.id());
    doc.setCustomerId(agg.getCustomerId());
    doc.setAccountType(agg.getAccountType());
    doc.setInitialDeposit(agg.getInitialDeposit());
    doc.setSortCode(agg.getSortCode());
    doc.setStatus(agg.getStatus());
    doc.setOpened(agg.isOpened());
    doc.setClosed(agg.isClosed());
    doc.setVersion(agg.getVersion());
    return doc;
  }

  AccountAggregate toAggregate(AccountDocument doc) {
    AccountAggregate agg = new AccountAggregate(doc.getId());
    AggregateFieldAccess.set(agg, "customerId", doc.getCustomerId());
    AggregateFieldAccess.set(agg, "accountType", doc.getAccountType());
    AggregateFieldAccess.set(agg, "initialDeposit", doc.getInitialDeposit());
    AggregateFieldAccess.set(agg, "sortCode", doc.getSortCode());
    AggregateFieldAccess.set(agg, "status", doc.getStatus());
    AggregateFieldAccess.set(agg, "opened", doc.isOpened());
    AggregateFieldAccess.set(agg, "closed", doc.isClosed());
    AggregateFieldAccess.set(agg, "version", doc.getVersion());
    return agg;
  }
}
