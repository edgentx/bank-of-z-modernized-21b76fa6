package com.example.domain.statement.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;
import java.time.Instant;

public record GenerateStatementCmd(
    String statementId,
    String accountNumber,
    Instant periodEnd,
    BigDecimal openingBalance,
    BigDecimal closingBalance
) implements Command {}
