package com.example.domain.account.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class AccountAggregate extends AggregateRoot {
  private final String accountId;
  private String customerId;
  private String accountType;
  private String currency;
  private BigDecimal balance = BigDecimal.ZERO;
  private boolean opened;

  public AccountAggregate(String accountId) {
    this.accountId = accountId;
  }

  @Override public String id() { return accountId; }

  @Override public List<DomainEvent> execute(Command cmd) {
    if (cmd instanceof OpenAccountCmd c) {
      if (opened) throw new IllegalStateException("Account already opened: " + c.accountId());
      if (c.customerId() == null || c.customerId().isBlank()) throw new IllegalArgumentException("customerId required");
      if (c.accountType() == null || c.accountType().isBlank()) throw new IllegalArgumentException("accountType required");
      if (c.currency() == null || c.currency().length() != 3) throw new IllegalArgumentException("ISO 4217 currency required");
      var event = new AccountOpenedEvent(c.accountId(), c.customerId(), c.accountType(), c.currency(), Instant.now());
      this.customerId = c.customerId();
      this.accountType = c.accountType();
      this.currency = c.currency();
      this.opened = true;
      addEvent(event);
      incrementVersion();
      return List.of(event);
    }
    throw new UnknownCommandException(cmd);
  }

  public boolean isOpened() { return opened; }
  public String getCustomerId() { return customerId; }
  public String getAccountType() { return accountType; }
  public String getCurrency() { return currency; }
  public BigDecimal getBalance() { return balance; }
}
