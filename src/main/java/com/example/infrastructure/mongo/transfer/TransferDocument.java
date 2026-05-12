package com.example.infrastructure.mongo.transfer;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

/**
 * MongoDB persistence model for {@link com.example.domain.transfer.model.TransferAggregate}.
 *
 * Indexed on the from/to account ids — both directions are queried during
 * reconciliation, and on {@code status} for completion-monitoring dashboards.
 */
@Document(collection = "transfers")
public class TransferDocument {
  @Id
  private String id;
  @Indexed
  private String fromAccountId;
  @Indexed
  private String toAccountId;
  @Indexed
  private String status;
  private BigDecimal amount;
  private String currency;
  private int version;

  public TransferDocument() {}

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public String getFromAccountId() { return fromAccountId; }
  public void setFromAccountId(String fromAccountId) { this.fromAccountId = fromAccountId; }
  public String getToAccountId() { return toAccountId; }
  public void setToAccountId(String toAccountId) { this.toAccountId = toAccountId; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public BigDecimal getAmount() { return amount; }
  public void setAmount(BigDecimal amount) { this.amount = amount; }
  public String getCurrency() { return currency; }
  public void setCurrency(String currency) { this.currency = currency; }
  public int getVersion() { return version; }
  public void setVersion(int version) { this.version = version; }
}
