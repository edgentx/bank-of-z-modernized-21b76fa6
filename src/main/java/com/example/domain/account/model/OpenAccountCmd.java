package com.example.domain.account.model;

import com.example.domain.shared.Command;

import java.math.BigDecimal;

/**
 * Command to open a new bank account.
 * Part of the Account Opening Saga (S-5).
 */
public record OpenAccountCmd(
        String accountId,
        String customerId,
        String accountType,
        BigDecimal initialDeposit,
        String sortCode
) implements Command {}
