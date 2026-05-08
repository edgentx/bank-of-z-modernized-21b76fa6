package com.example.domain.statement.model;

import com.example.domain.shared.Command;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Internal command to setup the Statement aggregate for testing/processing.
 * Represents the creation of the statement data.
 */
public record GenerateStatementCmd(
        String statementId,
        String accountId,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal openingBalance,
        BigDecimal credits,
        BigDecimal debits,
        BigDecimal closingBalance
) implements Command {}
