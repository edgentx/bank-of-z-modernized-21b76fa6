package com.example.application.transaction;

import com.example.application.AggregateNotFoundException;
import com.example.domain.transaction.model.PostDepositCmd;
import com.example.domain.transaction.model.PostWithdrawalCmd;
import com.example.domain.transaction.model.ReverseTransactionCmd;
import com.example.domain.transaction.model.TransactionAggregate;
import com.example.domain.transaction.repository.TransactionRepository;
import org.springframework.stereotype.Service;

@Service
public class TransactionAppService {

  private final TransactionRepository repository;

  public TransactionAppService(TransactionRepository repository) {
    this.repository = repository;
  }

  public TransactionAggregate postDeposit(PostDepositCmd cmd) {
    TransactionAggregate aggregate = repository
        .findById(cmd.transactionId())
        .orElseGet(() -> new TransactionAggregate(cmd.transactionId()));
    aggregate.execute(cmd);
    repository.save(aggregate);
    return aggregate;
  }

  public TransactionAggregate postWithdrawal(PostWithdrawalCmd cmd) {
    TransactionAggregate aggregate = repository
        .findById(cmd.transactionId())
        .orElseGet(() -> new TransactionAggregate(cmd.transactionId()));
    aggregate.execute(cmd);
    repository.save(aggregate);
    return aggregate;
  }

  public TransactionAggregate reverse(String transactionId, ReverseTransactionCmd cmd) {
    TransactionAggregate aggregate = repository
        .findById(transactionId)
        .orElseThrow(() -> new AggregateNotFoundException("Transaction", transactionId));
    aggregate.execute(cmd);
    repository.save(aggregate);
    return aggregate;
  }

  public TransactionAggregate findById(String transactionId) {
    return repository
        .findById(transactionId)
        .orElseThrow(() -> new AggregateNotFoundException("Transaction", transactionId));
  }
}
