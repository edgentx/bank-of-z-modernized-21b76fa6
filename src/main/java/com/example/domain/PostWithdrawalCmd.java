package com.example.domain;

import java.math.BigDecimal;
import java.util.UUID;

public record PostWithdrawalCmd(
    UUID transactionId,
    String accountNumber,
    BigDecimal amount,
    String currency
) {
    public PostWithdrawalCmd {
        if (transactionId == null) throw new IllegalArgumentException("transactionId cannot be null");
        if (accountNumber == null) throw new IllegalArgumentException("accountNumber cannot be null");
        if (currency == null || currency.isEmpty()) throw new IllegalArgumentException("currency cannot be null");
        // Amount validation happens in the Aggregate execute method
    }
}
