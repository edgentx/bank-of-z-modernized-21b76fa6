package com.example.domain.account.repository;

import com.example.domain.account.model.AccountAggregate;
import java.util.Optional;

public interface AccountRepository {
  Optional<AccountAggregate> findById(String accountId);
  void save(AccountAggregate aggregate);
}
