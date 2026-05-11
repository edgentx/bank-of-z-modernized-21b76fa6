package com.example.domain.account.repository;

import com.example.domain.account.model.AccountAggregate;
import java.util.Optional;

/**
 * Repository interface for Account aggregates.
 * Implementations (In-Memory, PostgreSQL, Mongo) must implement this interface.
 */
public interface AccountRepository {

    void save(AccountAggregate aggregate);

    Optional<AccountAggregate> findById(String id);

    // Optional: delete if needed for cleanup, though usually aggregates are just marked closed.
    // void delete(String id);
}
