package com.example.domain;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Command to post a deposit to a transaction/account.
 */
public record PostDepositCmd(
        String accountNumber,
        BigDecimal amount,
        String currency
) {
    public PostDepositCmd {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("AccountNumber cannot be null");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency cannot be null");
        }
        // Note: Amount validation (<= 0) is a business rule handled by the aggregate logic,
        // not a structural invariant of the DTO, but we can defensively copy.
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
    }
}