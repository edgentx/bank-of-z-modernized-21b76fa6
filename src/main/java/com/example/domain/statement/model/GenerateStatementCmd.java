package com.example.domain.statement.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Command to generate a new account statement for a specific period.
 * Story S-8.
 */
public record GenerateStatementCmd(
    String statementId,
    String accountNumber,
    Instant periodStart,
    Instant periodEnd,
    BigDecimal openingBalance,
    BigDecimal closingBalance,
    BigDecimal previousClosingBalance // Needed to verify invariant
) implements Command {}
