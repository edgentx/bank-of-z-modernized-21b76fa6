package com.example.domain.account.repository;

import com.example.domain.account.model.AccountAggregate;

public interface AccountRepository {
    void save(AccountAggregate aggregate);
    AccountAggregate load(String id);
}
