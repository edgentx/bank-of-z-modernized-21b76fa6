package com.example.infrastructure.mongo.transaction;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

/**
 * MongoDB persistence model for {@link com.example.domain.transaction.model.TransactionAggregate}.
 *
 * Indexed on {@code accountId} (list-txns-by-account is the dominant read path)
 * and {@code kind} (deposit/withdrawal slicing for reconciliation totals).
 */
@Document(collection = "transactions")
public class TransactionDocument {
  @Id
  private String id;
  @Indexed
  private String accountId;
  @Indexed
  private String kind;
  private BigDecimal amount;
  private String currency;
  private boolean posted;
  private boolean reversed;
  private int version;

  public TransactionDocument() {}

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public String getAccountId() { return accountId; }
  public void setAccountId(String accountId) { this.accountId = accountId; }
  public String getKind() { return kind; }
  public void setKind(String kind) { this.kind = kind; }
  public BigDecimal getAmount() { return amount; }
  public void setAmount(BigDecimal amount) { this.amount = amount; }
  public String getCurrency() { return currency; }
  public void setCurrency(String currency) { this.currency = currency; }
  public boolean isPosted() { return posted; }
  public void setPosted(boolean posted) { this.posted = posted; }
  public boolean isReversed() { return reversed; }
  public void setReversed(boolean reversed) { this.reversed = reversed; }
  public int getVersion() { return version; }
  public void setVersion(int version) { this.version = version; }
}
