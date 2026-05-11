package com.example.domain.account.repository;

import com.example.domain.account.model.AccountAggregate;
import java.util.Optional;

/**
 * Repository interface for Account aggregates.
 */
public interface AccountRepository {
    AccountAggregate save(AccountAggregate aggregate);
    Optional<AccountAggregate> findById(String id);
}
