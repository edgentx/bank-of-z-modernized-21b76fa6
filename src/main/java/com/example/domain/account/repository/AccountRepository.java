package com.example.domain.account.repository;

import com.example.domain.account.model.AccountAggregate;

public interface AccountRepository {
    AccountAggregate load(String accountNumber);
    void save(AccountAggregate aggregate);
}
