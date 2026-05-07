package com.example.domain;

import java.math.BigDecimal;

public record WithdrawalPostedEvent(
    TransactionId transactionId,
    AccountNumber accountNumber,
    BigDecimal amount,
    String currencyCode,
    BigDecimal balanceAfter
) implements S11Event {
}
