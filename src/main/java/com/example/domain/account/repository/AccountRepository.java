package com.example.domain.account.repository;

import com.example.domain.account.model.AccountAggregate;

public interface AccountRepository {
    AccountAggregate load(String id);
    void save(AccountAggregate aggregate);
    boolean existsByAccountNumber(String number);
}