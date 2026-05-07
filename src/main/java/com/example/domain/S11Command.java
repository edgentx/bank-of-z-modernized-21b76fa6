package com.example.domain;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Command hierarchy for Story S-11.
 * Following the Execute pattern, the Aggregate accepts a generic Command
 * and dispatches to the specific logic.
 */
public sealed interface S11Command permits S11Command.PostWithdrawalCmd {

    record PostWithdrawalCmd(
        UUID accountId,
        BigDecimal amount,
        String currency
    ) implements S11Command {
        public PostWithdrawalCmd {
            if (accountId == null) throw new IllegalArgumentException("AccountId cannot be null");
            if (amount == null) throw new IllegalArgumentException("Amount cannot be null");
            if (currency == null || currency.isBlank()) throw new IllegalArgumentException("Currency cannot be blank");
        }
    }

}
