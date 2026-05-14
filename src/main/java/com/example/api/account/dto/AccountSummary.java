package com.example.api.account.dto;

import com.example.domain.account.model.AccountAggregate;

public record AccountSummary(
    String accountId,
    String accountNumber,
    String customerId,
    String customerName,
    String accountType,
    String status,
    long balance,
    String currency,
    String sortCode,
    String openedAt
) {
  public static AccountSummary from(AccountAggregate agg) {
    return new AccountSummary(
        agg.id(),
        agg.id(),
        agg.getCustomerId(),
        agg.id(),
        agg.getAccountType(),
        agg.getStatus(),
        agg.getInitialDeposit(),
        "GBP",
        agg.getSortCode(),
        null);
  }
}
