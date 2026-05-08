package com.example.domain.statement.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Command to generate a new account statement for a specific period.
 * S-8: Statement Generation.
 */
public record GenerateStatementCmd(
        String statementId,
        String accountNumber,
        Instant periodStart,
        Instant periodEnd,
        BigDecimal openingBalance,
        BigDecimal closingBalance,
        BigDecimal previousClosingBalance // Required for validation
) implements Command {
}