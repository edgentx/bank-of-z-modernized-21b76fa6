package com.example.domain.statement.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Command to generate a new statement for a given period.
 * BNK-S-8
 */
public record GenerateStatementCmd(
        String statementId,
        String accountNumber,
        Instant periodEnd,
        BigDecimal openingBalance,
        BigDecimal closingBalanceOfPreviousStatement
) implements Command {
}
