package com.example.domain.account.repository;

import com.example.domain.account.model.AccountAggregate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryAccountRepository {
    private final Map<String, AccountAggregate> store = new HashMap<>();

    public void save(AccountAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    public AccountAggregate load(String id) {
        return store.get(id);
    }
}
