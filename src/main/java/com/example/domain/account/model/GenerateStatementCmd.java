package com.example.domain.account.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Command to generate a new statement.
 * S-8: Implement GenerateStatementCmd on Statement.
 */
public record GenerateStatementCmd(
    String statementId,
    String accountNumber,
    LocalDate periodEnd,
    BigDecimal openingBalance
) implements Command {}
