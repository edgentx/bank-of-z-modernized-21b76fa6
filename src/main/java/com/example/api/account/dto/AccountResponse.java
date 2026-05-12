package com.example.api.account.dto;

import com.example.domain.account.model.AccountAggregate;

public record AccountResponse(
    String accountId,
    String customerId,
    String accountType,
    long initialDeposit,
    String sortCode,
    String status,
    boolean opened,
    boolean closed,
    int version
) {
  public static AccountResponse from(AccountAggregate agg) {
    return new AccountResponse(
        agg.id(),
        agg.getCustomerId(),
        agg.getAccountType(),
        agg.getInitialDeposit(),
        agg.getSortCode(),
        agg.getStatus(),
        agg.isOpened(),
        agg.isClosed(),
        agg.getVersion());
  }
}
