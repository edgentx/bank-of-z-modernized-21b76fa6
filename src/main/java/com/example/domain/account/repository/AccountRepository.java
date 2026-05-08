package com.example.domain.account.repository;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.shared.Aggregate;

import java.util.Optional;

public interface AccountRepository {
    void save(AccountAggregate aggregate);
    Optional<AccountAggregate> findByAccountNumber(String accountNumber);
}