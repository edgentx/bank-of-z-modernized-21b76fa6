package com.example.infrastructure.mongo.account;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MongoDB persistence model for {@link com.example.domain.account.model.AccountAggregate}.
 *
 * Indexed fields:
 *   - customerId (top-of-mind lookup: list accounts for a customer),
 *   - sortCode   (branch / routing-driven reporting paths),
 *   - status     (filter by Active/Closed across statement & reconciliation
 *                 flows).
 */
@Document(collection = "accounts")
public class AccountDocument {
  @Id
  private String id;
  @Indexed
  private String customerId;
  @Indexed
  private String sortCode;
  @Indexed
  private String status;
  private String accountType;
  private long initialDeposit;
  private boolean opened;
  private boolean closed;
  private int version;

  public AccountDocument() {}

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public String getCustomerId() { return customerId; }
  public void setCustomerId(String customerId) { this.customerId = customerId; }
  public String getAccountType() { return accountType; }
  public void setAccountType(String accountType) { this.accountType = accountType; }
  public long getInitialDeposit() { return initialDeposit; }
  public void setInitialDeposit(long initialDeposit) { this.initialDeposit = initialDeposit; }
  public String getSortCode() { return sortCode; }
  public void setSortCode(String sortCode) { this.sortCode = sortCode; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public boolean isOpened() { return opened; }
  public void setOpened(boolean opened) { this.opened = opened; }
  public boolean isClosed() { return closed; }
  public void setClosed(boolean closed) { this.closed = closed; }
  public int getVersion() { return version; }
  public void setVersion(int version) { this.version = version; }
}
