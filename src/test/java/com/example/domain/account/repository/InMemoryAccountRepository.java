package com.example.domain.account.repository;

import com.example.domain.account.model.AccountAggregate;
import java.util.HashMap;
import java.util.Map;

public class InMemoryAccountRepository {
    private final Map<String, AccountAggregate> store = new HashMap<>();

    public void save(AccountAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    public AccountAggregate findById(String id) {
        return store.get(id);
    }
}
