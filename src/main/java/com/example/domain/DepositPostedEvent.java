package com.example.domain;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Event emitted when a deposit is successfully posted.
 * Replaces the misspelled/dupe 'DepostedEvent'.
 */
public record DepositPostedEvent(
        String transactionId,
        String accountNumber,
        BigDecimal amount,
        Currency currency
) {}
