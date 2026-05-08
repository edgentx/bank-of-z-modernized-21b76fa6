package com.example.domain.statement.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Command to generate a new account statement.
 * S-8
 */
public record GenerateStatementCmd(
    String statementId,
    String accountNumber,
    LocalDate periodStart,
    LocalDate periodEnd,
    BigDecimal openingBalance,
    BigDecimal closingBalance
) implements Command {}
