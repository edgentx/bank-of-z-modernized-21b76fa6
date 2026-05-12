package com.example.infrastructure.db2.history;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * JPA mapping for a legacy DB2 account-history row (BANK S-29).
 *
 * Read-only view of pre-migration account snapshots. The modernized write-
 * side lives on the {@link com.example.domain.account.model.AccountAggregate}
 * Mongo adapter; this row class only exists so reporting and statement flows
 * can traverse legacy data during cutover.
 */
@Entity
@Table(name = "account_history")
public class AccountHistoryEntity {

  @Id
  @Column(name = "account_id", length = 64, nullable = false)
  private String accountId;

  @Column(name = "customer_id", length = 64, nullable = false)
  private String customerId;

  @Column(name = "sort_code", length = 16)
  private String sortCode;

  @Column(name = "account_type", length = 32)
  private String accountType;

  @Column(name = "status", length = 16, nullable = false)
  private String status;

  @Column(name = "opened_at")
  private Instant openedAt;

  @Column(name = "closed_at")
  private Instant closedAt;

  @Column(name = "legacy_source", length = 32, nullable = false)
  private String legacySource;

  protected AccountHistoryEntity() {}

  public AccountHistoryEntity(
      String accountId,
      String customerId,
      String sortCode,
      String accountType,
      String status,
      Instant openedAt,
      Instant closedAt,
      String legacySource) {
    this.accountId = accountId;
    this.customerId = customerId;
    this.sortCode = sortCode;
    this.accountType = accountType;
    this.status = status;
    this.openedAt = openedAt;
    this.closedAt = closedAt;
    this.legacySource = legacySource;
  }

  public String getAccountId() { return accountId; }
  public String getCustomerId() { return customerId; }
  public String getSortCode() { return sortCode; }
  public String getAccountType() { return accountType; }
  public String getStatus() { return status; }
  public Instant getOpenedAt() { return openedAt; }
  public Instant getClosedAt() { return closedAt; }
  public String getLegacySource() { return legacySource; }
}
