package com.example.domain.statement.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Command to generate a new account statement.
 * Validations:
 * - Period must be closed (periodEnd < today)
 * - Opening balance must match previous statement closing balance
 */
public record GenerateStatementCmd(
        String statementId,
        String accountNumber,
        LocalDate periodStart,
        LocalDate periodEnd,
        BigDecimal openingBalance
) implements Command {}
