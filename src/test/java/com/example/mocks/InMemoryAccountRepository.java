package com.example.mocks;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.repository.AccountRepository;

import java.util.HashMap;
import java.util.Map;

public class InMemoryAccountRepository implements AccountRepository {
    private final Map<String, AccountAggregate> store = new HashMap<>();

    @Override
    public void save(AccountAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    @Override
    public AccountAggregate load(String id) {
        AccountAggregate a = store.get(id);
        if (a == null) {
            // Return a fresh transient aggregate if not found (Test convenience)
            return new AccountAggregate(id);
        }
        return a;
    }
}
