package com.example.domain.statement.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Command to generate a new account statement.
 * Immutable record (Java 21+ or compatible).
 */
public record GenerateStatementCmd(
        String statementId,
        String accountNumber,
        Instant periodEnd,
        BigDecimal openingBalance
) implements Command {}
