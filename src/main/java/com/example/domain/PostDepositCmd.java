package com.example.domain;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

/**
 * Command to credit funds to a specific account.
 * Implemented as a Java Record for immutability and data carrier semantics.
 */
public record PostDepositCmd(
    UUID transactionId,
    String accountNumber,
    BigDecimal amount,
    Currency currency
) {
}
