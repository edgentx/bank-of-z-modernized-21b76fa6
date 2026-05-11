package com.example.mocks;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.repository.AccountRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemoryAccountRepository implements AccountRepository {
    private final Map<String, AccountAggregate> store = new HashMap<>();

    @Override
    public AccountAggregate save(AccountAggregate aggregate) {
        // In a real repo, we'd check for version conflicts, but for mocks simple put is fine.
        // Assuming the aggregate ID is already set.
        store.put(aggregate.id(), aggregate);
        return aggregate;
    }

    @Override
    public Optional<AccountAggregate> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    // Helper for testing to ensure we don't accidentally find existing IDs
    public String generateNewId() {
        return UUID.randomUUID().toString();
    }
}