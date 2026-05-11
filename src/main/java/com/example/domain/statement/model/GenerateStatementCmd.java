package com.example.domain.statement.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Command to generate a new statement for an account.
 * @param statementId The ID of the statement to generate.
 * @param accountNumber The account number.
 * @param periodEnd The end date of the statement period.
 * @param openingBalance The opening balance for the period.
 * @param previousClosingBalance The closing balance of the previous period (for validation).
 * @param generatedAt The time the generation was requested.
 */
public record GenerateStatementCmd(
    String statementId,
    String accountNumber,
    Instant periodEnd,
    BigDecimal openingBalance,
    BigDecimal previousClosingBalance,
    Instant generatedAt
) implements Command {}
