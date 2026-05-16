package com.example.api.account.dto;

import com.example.domain.account.model.AccountAggregate;
import java.time.Instant;

public record AccountResponse(
    String accountId,
    String accountNumber,
    String customerId,
    String customerName,
    String accountType,
    String status,
    long balance,
    String currency,
    String sortCode,
    String openedAt,
    long availableBalance,
    long overdraftLimit,
    String branch,
    String updatedAt,
    long initialDeposit,
    boolean opened,
    boolean closed,
    int version
) {
  public static AccountResponse from(AccountAggregate agg) {
    return from(agg, agg.getCustomerId());
  }

  public static AccountResponse from(AccountAggregate agg, String customerName) {
    String timestamp = Instant.now().toString();
    return new AccountResponse(
        agg.id(),
        agg.id(),
        agg.getCustomerId(),
        customerName,
        agg.getAccountType(),
        agg.getStatus(),
        agg.getInitialDeposit(),
        "GBP",
        agg.getSortCode(),
        timestamp,
        agg.getInitialDeposit(),
        0,
        agg.getSortCode(),
        timestamp,
        agg.getInitialDeposit(),
        agg.isOpened(),
        agg.isClosed(),
        agg.getVersion());
  }
}
