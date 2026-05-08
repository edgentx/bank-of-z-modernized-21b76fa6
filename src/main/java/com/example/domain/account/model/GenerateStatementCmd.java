package com.example.domain.account.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;
import java.time.Instant;

public record GenerateStatementCmd(
        String statementId,
        String accountNumber,
        Instant periodStart,
        Instant periodEnd,
        BigDecimal openingBalance
) implements Command {
    // Validations for parameters can be added here or in the aggregate
}
