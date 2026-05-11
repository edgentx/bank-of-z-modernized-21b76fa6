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
    public Optional<Account> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        return store.values().stream()
                .filter(acc -> acc.getAccountNumber() != null && acc.getAccountNumber().equals(accountNumber))
                .findFirst();
    }
}
