package com.example.domain.account.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Command to generate a new account statement.
 * S-8: Statement Generation.
 */
public record GenerateStatementCmd(
    String statementId,
    String accountNumber,
    Instant periodStart,
    Instant periodEnd,
    BigDecimal openingBalance,
    BigDecimal closingBalance,
    Instant previousStatementClosingDate
) implements Command {}
