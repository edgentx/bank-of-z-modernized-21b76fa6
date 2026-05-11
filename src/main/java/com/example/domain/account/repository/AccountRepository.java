package com.example.domain.account.repository;

import com.example.domain.account.model.AccountAggregate;

/**
 * Repository interface for Account aggregates.
 */
public interface AccountRepository {
    AccountAggregate save(AccountAggregate aggregate);
    AccountAggregate findById(String id);
}
