package com.example.domain.statement.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;
import java.time.LocalDate;

public record GenerateStatementCmd(
        String statementId,
        String accountNumber,
        LocalDate periodEnd,
        LocalDate previousStatementDate,
        LocalDate periodStart,
        BigDecimal previousClosingBalance,
        BigDecimal openingBalance,
        BigDecimal closingBalance
) implements Command {}
