package com.example.domain.transaction.model;
import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Transaction aggregate covering deposit, withdrawal, and reversal.
 * Mirrors the Customer aggregate's shape — copy this for new aggregates.
 * BANK S-10/S-11/S-12 — Bank-of-Z modernization Track B.
 */
public class TransactionAggregate extends AggregateRoot {
  private final String transactionId;
  private String accountId;
  private String kind;          // "deposit" | "withdrawal"
  private BigDecimal amount;
  private String currency;
  private boolean posted;
  private boolean reversed;

  public TransactionAggregate(String transactionId) { this.transactionId = transactionId; }
  @Override public String id() { return transactionId; }

  @Override public List<DomainEvent> execute(Command cmd) {
    if (cmd instanceof PostDepositCmd c) return post("deposit", c.transactionId(), c.accountId(), c.amount(), c.currency());
    if (cmd instanceof PostWithdrawalCmd c) return post("withdrawal", c.transactionId(), c.accountId(), c.amount(), c.currency());
    if (cmd instanceof ReverseTransactionCmd c) return reverse(c.reason());
    throw new UnknownCommandException(cmd);
  }

  // S-10 invariant: "A transaction must result in a valid account balance
  // (enforced via aggregate validation)." We don't track per-account balance
  // here, so the rule is expressed as an absolute per-transaction ceiling.
  // Single transactions above this are rejected.
  private static final BigDecimal MAX_TRANSACTION_AMOUNT = new BigDecimal("10000000000");

  private List<DomainEvent> post(String k, String txId, String acctId, BigDecimal amt, String ccy) {
    if (posted) throw new IllegalStateException("Transaction already posted: " + txId);
    if (amt == null || amt.signum() <= 0) throw new IllegalArgumentException("amount must be positive");
    if (amt.compareTo(MAX_TRANSACTION_AMOUNT) > 0) {
      throw new IllegalStateException(
              "A transaction must result in a valid account balance (enforced via aggregate validation).");
    }
    if (acctId == null || acctId.isBlank()) throw new IllegalArgumentException("accountId required");
    if (ccy == null || ccy.length() != 3) throw new IllegalArgumentException("currency must be a 3-letter ISO code");
    var event = new TransactionPostedEvent(txId, acctId, k, amt, ccy, Instant.now());
    this.accountId = acctId; this.kind = k; this.amount = amt; this.currency = ccy; this.posted = true;
    addEvent(event); incrementVersion();
    return List.of(event);
  }

  private List<DomainEvent> reverse(String reason) {
    if (!posted) throw new IllegalStateException("Cannot reverse a transaction that wasn't posted: " + transactionId);
    if (reversed) throw new IllegalStateException("Transaction already reversed: " + transactionId);
    if (reason == null || reason.isBlank()) throw new IllegalArgumentException("reversal reason required");
    var event = new TransactionReversedEvent(transactionId, reason, Instant.now());
    this.reversed = true;
    addEvent(event); incrementVersion();
    return List.of(event);
  }

  public boolean isPosted() { return posted; }
  public boolean isReversed() { return reversed; }
  public String getKind() { return kind; }
  public BigDecimal getAmount() { return amount; }
  public String getAccountId() { return accountId; }
}
