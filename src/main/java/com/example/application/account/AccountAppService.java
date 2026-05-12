package com.example.application.account;

import com.example.application.AggregateNotFoundException;
import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.CloseAccountCmd;
import com.example.domain.account.model.OpenAccountCmd;
import com.example.domain.account.model.UpdateAccountStatusCmd;
import com.example.domain.account.repository.AccountRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountAppService {

  private final AccountRepository repository;

  public AccountAppService(AccountRepository repository) {
    this.repository = repository;
  }

  public AccountAggregate open(OpenAccountCmd cmd) {
    AccountAggregate aggregate = repository
        .findById(cmd.accountId())
        .orElseGet(() -> new AccountAggregate(cmd.accountId()));
    aggregate.execute(cmd);
    repository.save(aggregate);
    return aggregate;
  }

  public AccountAggregate updateStatus(String accountId, UpdateAccountStatusCmd cmd) {
    AccountAggregate aggregate = repository
        .findById(accountId)
        .orElseThrow(() -> new AggregateNotFoundException("Account", accountId));
    aggregate.execute(cmd);
    repository.save(aggregate);
    return aggregate;
  }

  public AccountAggregate close(String accountId, CloseAccountCmd cmd) {
    AccountAggregate aggregate = repository
        .findById(accountId)
        .orElseThrow(() -> new AggregateNotFoundException("Account", accountId));
    aggregate.execute(cmd);
    repository.save(aggregate);
    return aggregate;
  }

  public AccountAggregate findById(String accountId) {
    return repository
        .findById(accountId)
        .orElseThrow(() -> new AggregateNotFoundException("Account", accountId));
  }
}
