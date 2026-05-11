package com.example.domain.account.repository;

import com.example.domain.account.model.AccountAggregate;

import java.util.Optional;

/**
 * Repository interface for Account aggregates.
 */
public interface AccountRepository {
    Optional<AccountAggregate> findByAccountNumber(String accountNumber);
    void save(AccountAggregate aggregate);
}
