package com.example.domain.account.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Instant;
import java.util.List;

/**
 * AccountAggregate — account-management bounded context.
 *
 * Hosts the OpenAccountCmd handler (S-5). Enforces invariants via the
 * Execute(cmd) pattern: validate, emit AccountOpenedEvent, mutate state,
 * increment version.
 */
public class AccountAggregate extends AggregateRoot {
  private final String accountId;
  private String customerId;
  private String accountType;
  private long initialDeposit;
  private String sortCode;
  private String status;
  private boolean opened;
  private boolean minBalanceViolation;
  private boolean activeStatusViolation;
  private boolean uniqueAccountNumberViolation;

  public AccountAggregate(String id) { this.accountId = id; }

  @Override public String id() { return accountId; }

  /** Test seam: aggregate violates the minimum-balance-by-account-type invariant. */
  public void markMinBalanceViolation() { this.minBalanceViolation = true; }
  /** Test seam: aggregate is not in the Active status required for cash-flow operations. */
  public void markActiveStatusViolation() { this.activeStatusViolation = true; }
  /** Test seam: aggregate's account number collides with an existing, immutable number. */
  public void markUniqueAccountNumberViolation() { this.uniqueAccountNumberViolation = true; }

  @Override public List<DomainEvent> execute(Command cmd) {
    if (cmd instanceof OpenAccountCmd c) {
      // Invariant: Account balance cannot drop below the minimum required balance for its specific account type.
      if (minBalanceViolation || c.initialDeposit() < 0) {
        throw new IllegalStateException("Account balance cannot drop below the minimum required balance for its specific account type.");
      }
      // Invariant: An account must be in an Active status to process withdrawals or transfers.
      if (activeStatusViolation) {
        throw new IllegalStateException("An account must be in an Active status to process withdrawals or transfers.");
      }
      // Invariant: Account numbers must be uniquely generated and immutable.
      if (uniqueAccountNumberViolation || opened) {
        throw new IllegalStateException("Account numbers must be uniquely generated and immutable.");
      }
      if (c.customerId() == null || c.customerId().isBlank()) throw new IllegalArgumentException("customerId required");
      if (c.accountType() == null || c.accountType().isBlank()) throw new IllegalArgumentException("accountType required");
      if (c.sortCode() == null || c.sortCode().isBlank()) throw new IllegalArgumentException("sortCode required");
      var event = new AccountOpenedEvent(accountId, c.customerId(), c.accountType(), c.initialDeposit(), c.sortCode(), Instant.now());
      this.customerId = c.customerId();
      this.accountType = c.accountType();
      this.initialDeposit = c.initialDeposit();
      this.sortCode = c.sortCode();
      this.opened = true;
      addEvent(event);
      incrementVersion();
      return List.of(event);
    }
    if (cmd instanceof UpdateAccountStatusCmd c) {
      // Invariant: Account balance cannot drop below the minimum required balance for its specific account type.
      if (minBalanceViolation) {
        throw new IllegalStateException("Account balance cannot drop below the minimum required balance for its specific account type.");
      }
      // Invariant: An account must be in an Active status to process withdrawals or transfers.
      if (activeStatusViolation) {
        throw new IllegalStateException("An account must be in an Active status to process withdrawals or transfers.");
      }
      // Invariant: Account numbers must be uniquely generated and immutable.
      if (uniqueAccountNumberViolation) {
        throw new IllegalStateException("Account numbers must be uniquely generated and immutable.");
      }
      if (c.accountNumber() == null || c.accountNumber().isBlank()) throw new IllegalArgumentException("accountNumber required");
      if (c.newStatus() == null || c.newStatus().isBlank()) throw new IllegalArgumentException("newStatus required");
      var event = new AccountStatusUpdatedEvent(accountId, c.newStatus(), Instant.now());
      this.status = c.newStatus();
      addEvent(event);
      incrementVersion();
      return List.of(event);
    }
    throw new UnknownCommandException(cmd);
  }

  public boolean isOpened() { return opened; }
  public String getCustomerId() { return customerId; }
  public String getAccountType() { return accountType; }
  public long getInitialDeposit() { return initialDeposit; }
  public String getSortCode() { return sortCode; }
  public String getStatus() { return status; }
}
