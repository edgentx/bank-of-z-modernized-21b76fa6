package com.example.mocks;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.AccountStatus;
import com.example.domain.account.repository.AccountRepository;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class InMemoryAccountRepository implements AccountRepository {
    private final Map<String, AccountAggregate> store = new HashMap<>();

    @Override
    public Optional<AccountAggregate> findById(String accountId) {
        return Optional.ofNullable(store.get(accountId));
    }

    @Override
    public Page<AccountAggregate> list(String accountNumber, String customerId, AccountStatus status, Pageable pageable) {
        var filtered = store.values().stream()
            .filter(account -> accountNumber == null || accountNumber.isBlank() || account.id().contains(accountNumber))
            .filter(account -> customerId == null || customerId.isBlank() || customerId.equals(account.getCustomerId()))
            .filter(account -> status == null || status.name().equals(account.getStatus()))
            .sorted(Comparator.comparing(AccountAggregate::id))
            .toList();
        int start = Math.min((int) pageable.getOffset(), filtered.size());
        int end = Math.min(start + pageable.getPageSize(), filtered.size());
        return new PageImpl<>(filtered.subList(start, end), pageable, filtered.size());
    }

    @Override
    public void save(AccountAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }
}
