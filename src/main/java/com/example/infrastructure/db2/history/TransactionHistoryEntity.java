package com.example.infrastructure.db2.history;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * JPA mapping for a legacy DB2 transaction-history row (BANK S-29).
 *
 * Read-only: the modernized application writes new transactions through the
 * {@link com.example.domain.transaction.model.TransactionAggregate} + Mongo
 * adapter pair. This entity exposes the historical DB2 rows for reporting and
 * cross-system reconciliation during the Strangler Fig cutover window.
 */
@Entity
@Table(name = "transaction_history")
public class TransactionHistoryEntity {

  @Id
  @Column(name = "transaction_id", length = 64, nullable = false)
  private String transactionId;

  @Column(name = "account_id", length = 64, nullable = false)
  private String accountId;

  @Column(name = "kind", length = 16, nullable = false)
  private String kind;

  @Column(name = "amount", precision = 19, scale = 4, nullable = false)
  private BigDecimal amount;

  @Column(name = "currency", length = 3, nullable = false)
  private String currency;

  @Column(name = "posted_at", nullable = false)
  private Instant postedAt;

  // SMALLINT 0/1 on DB2 — mapped to a Java boolean via Hibernate's NumericBooleanConverter
  // would normally be needed, but JPA can read SMALLINT into a short and the
  // accessor exposes the boolean view.
  @Column(name = "reversed", nullable = false)
  private short reversedFlag;

  @Column(name = "legacy_source", length = 32, nullable = false)
  private String legacySource;

  protected TransactionHistoryEntity() {}

  public TransactionHistoryEntity(
      String transactionId,
      String accountId,
      String kind,
      BigDecimal amount,
      String currency,
      Instant postedAt,
      boolean reversed,
      String legacySource) {
    this.transactionId = transactionId;
    this.accountId = accountId;
    this.kind = kind;
    this.amount = amount;
    this.currency = currency;
    this.postedAt = postedAt;
    this.reversedFlag = (short) (reversed ? 1 : 0);
    this.legacySource = legacySource;
  }

  public String getTransactionId() { return transactionId; }
  public String getAccountId() { return accountId; }
  public String getKind() { return kind; }
  public BigDecimal getAmount() { return amount; }
  public String getCurrency() { return currency; }
  public Instant getPostedAt() { return postedAt; }
  public boolean isReversed() { return reversedFlag != 0; }
  public String getLegacySource() { return legacySource; }
}
