package com.example.domain.account.repository;
import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.AccountStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
public interface AccountRepository {
  Optional<AccountAggregate> findById(String accountId);
  Page<AccountAggregate> list(String accountNumber, String customerId, AccountStatus status, Pageable pageable);
  void save(AccountAggregate aggregate);
}
