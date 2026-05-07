package com.example.mocks;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.repository.AccountRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class MockAccountRepository implements AccountRepository {
    private final Map<String, AccountAggregate> store = new HashMap<>();

    @Override
    public AccountAggregate save(AccountAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
        return aggregate;
    }

    @Override
    public AccountAggregate findById(String id) {
        return store.get(id);
    }
}
