package com.example.domain.account.model;

import com.example.domain.shared.Command;

import java.math.BigDecimal;

/**
 * Command to open a new bank account.
 */
public record OpenAccountCmd(
        String customerId,
        String accountType,
        BigDecimal initialDeposit,
        String sortCode
) implements Command {}
