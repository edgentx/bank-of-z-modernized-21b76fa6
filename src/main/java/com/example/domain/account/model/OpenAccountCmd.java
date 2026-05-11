package com.example.domain.account.model;

import com.example.domain.shared.Command;

import java.math.BigDecimal;

/**
 * Command to open a new bank account.
 */
public record OpenAccountCmd(
    String accountId,
    String customerId,
    AccountAggregate.AccountType accountType,
    BigDecimal initialDeposit,
    String sortCode
) implements Command {}
