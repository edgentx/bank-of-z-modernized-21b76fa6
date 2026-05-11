package com.example.domain.statement.model;

import com.example.domain.shared.Command;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Command to generate a new statement.
 */
public record GenerateStatementCmd(
    String statementId,
    String accountNumber,
    LocalDate periodEnd,
    BigDecimal openingBalance
) implements Command {}
