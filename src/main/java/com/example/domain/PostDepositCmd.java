package com.example.domain;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Immutable Command to post a deposit.
 */
public record PostDepositCmd(String accountNumber, BigDecimal amount, Currency currency) {}
