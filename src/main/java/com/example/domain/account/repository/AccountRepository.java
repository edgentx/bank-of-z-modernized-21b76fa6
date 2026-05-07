package com.example.domain.account.repository;

import com.example.domain.account.model.AccountAggregate;

public interface AccountRepository {
    AccountAggregate save(AccountAggregate aggregate);
    AccountAggregate findById(String id);
}
