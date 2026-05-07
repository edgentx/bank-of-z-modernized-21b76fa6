package com.example.mocks;
import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.repository.AccountRepository;
import java.util.*;
public class InMemoryAccountRepository implements AccountRepository {
    private final Map<String, AccountAggregate> store = new HashMap<>();
    @Override public void save(AccountAggregate a) { store.put(a.id(), a); }
    @Override public Optional<AccountAggregate> findById(String id) { return Optional.ofNullable(store.get(id)); }
}