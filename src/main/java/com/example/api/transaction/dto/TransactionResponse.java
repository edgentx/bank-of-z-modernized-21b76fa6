package com.example.api.transaction.dto;

import com.example.domain.transaction.model.TransactionAggregate;
import java.math.BigDecimal;

public record TransactionResponse(
    String transactionId,
    String accountId,
    String kind,
    BigDecimal amount,
    boolean posted,
    boolean reversed,
    int version
) {
  public static TransactionResponse from(TransactionAggregate agg) {
    return new TransactionResponse(
        agg.id(),
        agg.getAccountId(),
        agg.getKind(),
        agg.getAmount(),
        agg.isPosted(),
        agg.isReversed(),
        agg.getVersion());
  }
}
