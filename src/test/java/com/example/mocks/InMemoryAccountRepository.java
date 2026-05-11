package com.example.mocks;

import com.example.domain.account.model.Account;
import com.example.domain.account.repository.AccountRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryAccountRepository implements AccountRepository {
    private final Map<String, Account> store = new HashMap<>();

    @Override
    public void save(Account account) {
        store.put(account.id(), account);
    }

    @Override
    public Optional<Account> findById(String accountNumber) {
        return Optional.ofNullable(store.get(accountNumber));
    }
}
