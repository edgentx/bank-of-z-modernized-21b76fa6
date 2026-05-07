package com.example.domain;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Event emitted when a deposit is successfully posted.
 */
public record DepositPostedEvent(String accountNumber, BigDecimal amount, Currency currency, java.time.LocalDateTime timestamp) {}
