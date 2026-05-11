package com.example.domain.account.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Command to open a new bank account.
 * Immutable record holding all necessary data for account creation.
 */
public record OpenAccountCmd(
    String commandId,
    String customerId,
    String accountType,
    BigDecimal initialDeposit,
    String sortCode,
    String accountNumber // Pre-generated or provided for validation
) implements Command {}
