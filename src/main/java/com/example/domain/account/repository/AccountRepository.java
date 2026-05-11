package com.example.domain.account.repository;

import com.example.domain.account.model.Account;

import java.util.Optional;

public interface AccountRepository {
    void save(Account account);
    Optional<Account> findById(String accountNumber);
}
