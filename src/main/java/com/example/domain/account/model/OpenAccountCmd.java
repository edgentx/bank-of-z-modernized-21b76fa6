package com.example.domain.account.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Command to open a new bank account.
 * This initiates the saga/durable workflow for account creation.
 */
public record OpenAccountCmd(
    String accountId,
    String customerId,
    String accountType,
    BigDecimal initialDeposit,
    String sortCode,
    String accountNumber // Optional: if null, one is generated or fetched from external service
) implements Command {

    public OpenAccountCmd {
        // Basic validation at the DTO level is optional if Aggregate handles it,
        // but good practice to ensure non-nulls for required fields if constructor usage varies.
        // However, keeping the record clean lets the Aggregate handle domain validation logic.
    }

    // Canonical constructor logic is implicit, but we ensure defaults if necessary
    // or just leave it to the Aggregate.
}
