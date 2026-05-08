package com.example.domain.account.repository;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.shared.Aggregate;
import java.util.Optional;

public interface AccountRepository {
    Optional<AccountAggregate> findById(String accountNumber);
    void save(AccountAggregate aggregate);
}
